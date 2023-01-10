#include "golomb.h"
#include "bitStream.h"
#include "opencv2/opencv.hpp"
#include "opencv2/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <vector>
#include <string>
#include <string.h>
#include <iostream>
#include "YUV4MPEG2Reader.h"

using namespace std;
using namespace cv;

int main(int argc, char* argv[]){

    //function that converts char to array of bits
    auto charToBits = [](char c) {
        vector<int> bits;
        for (int i = 7; i >= 0; i--) {
            bits.push_back((c >> i) & 1);
        }
        return bits;
    };

    //function to calculate m based on u
    auto calc_m = [](int u) {
        return (int) - (1/log((double) u / (1 + u)));
    };

    auto predict = [](int a, int b, int c) {
        if (c >= max(a, b))
            return min(a, b);   //min(left, above) if  left top >= max(left, above)
        else if (c <= min(a, b))
            return max(a, b);   //max(left, above) if  left top <= min(left, above)
        else
            return a + b - c;   //otherwise, left + above - left top
    };

    //Check for correct number of arguments
    if(argc != 7){
        cout << "Usage: " << argv[0] << " <input file> <output file> <block size> <search area> <key-frame period> <quantization>" << endl;
        return 1;
    }

    //Open input file
    FILE* input = fopen(argv[1], "r");
    if(input == NULL){
        cout << "Error: Could not open input file" << endl;
        return 1;
    }

    int blockSize = atoi(argv[3]);
    int searchDistance = atoi(argv[4]);
    int keyFramePeriod = atoi(argv[5]);
    int quantization = atoi(argv[6]);

    // //check if blocksize is a power of 2
    // if((blockSize & (blockSize - 1)) != 0){
    //     cout << "Error: Block size must be a power of 2" << endl;
    //     return 1;
    // }



    YUV4MPEG2Reader reader(argv[1]);

    int width = reader.width();
    int height = reader.height();

    //Checking inputs
    //check if the blocksize is < width
    if(blockSize > width){
        cout << "Error: Block size must be less than width" << endl;
        return 1;
    }
    if(blockSize + searchDistance > width || blockSize + searchDistance > height){
        cout << "Error: Search area + block size must be less than width and heigth" << endl;
        return 1;
    }
    if(keyFramePeriod < 1){
        cout << "Error: Key-frame period must be greater than 0" << endl;
        return 1;
    }

    
    int colorSpace = stoi(reader.colorSpace());
    if(colorSpace != 420 && colorSpace != 422 && colorSpace != 444){
        cout << "Error: Color space not supported" << endl;
        return 1;
    }
    int aspectRatio1 = reader.aspectRatio1();
    int aspectRatio2 = reader.aspectRatio2();
    char interlace = reader.interlacing().at(0);
    vector<int> interlace_v = charToBits(interlace);
    int frameRate1 = reader.frameRate1();
    int frameRate2 = reader.frameRate2();

    //general variables (the encoded residuals and the m for each channel)
    string Yencoded = "";
    string Cbencoded = "";
    string Crencoded = "";
    vector<short> Ym, Cbm, Crm;
    vector<int> encoded_Ybits, encoded_Cbbits, encoded_Crbits;

    int padded_width = width;
    int padded_height = height;
    if (width % blockSize != 0) {
        padded_width = width + (blockSize - (width % blockSize));
    }
    if (height % blockSize != 0) {
        padded_height = height + (blockSize - (height % blockSize));
    }

    //start a timer
    clock_t start = clock();

    vector<int> Ym_vector;
    vector<int> Cbm_vector;
    vector<int> Crm_vector;
    vector<int> Yresiduals;
    vector<int> Cbresiduals;
    vector<int> Crresiduals;
    vector<int> Y, U, V;

    vector<int> motionVectorXs;
    vector<int> motionVectorYs;
    
    bool finish = false;

    int numFrames = 0;
    Y = vector<int>(width * height);

    if(colorSpace == 420){
        U = vector<int>(width * height / 4);
        V = vector<int>(width * height / 4);
    } else if(colorSpace == 422){
        U = vector<int>(width * height / 2);
        V = vector<int>(width * height / 2);
    } else if(colorSpace == 444){
        U = vector<int>(width * height);
        V = vector<int>(width * height);
    }
        
    char line[100]; //Read the line after the header
    fgets(line, 100, input);
    clock_t start2 = clock();

    //keyFrame variables
    int frameIndex = 0;
    Mat keyYmat, keyUmat, keyVmat;

    int totalYresiduals = 0;
    int totalCbresiduals = 0;
    int totalCrresiduals = 0;

    //reading frames
    while(!feof(input)){
        numFrames++;
        //read the frame line
        fgets(line, 100, input);
        //read the Y data  (Height x Width)
        for(int i = 0; i < width * height; i++){
            Y[i] = fgetc(input);   
            if(Y[i] < 0) {
                numFrames--;
                finish = true;
                break;
            }
        }
        if (finish) break;
        if (colorSpace == 420) {
            for(int i = 0; i < width * height / 4; i++) U[i] = fgetc(input); //read the U data (Height/2 x Width/2)
            for(int i = 0; i < width * height / 4; i++) V[i] = fgetc(input); //read the V data (Height/2 x Width/2)
        } else if(colorSpace == 422){
            for(int i = 0; i < width * height / 2; i++) U[i] = fgetc(input); //read the U data (Height x Width/2)
            for(int i = 0; i < width * height / 2; i++) V[i] = fgetc(input); //read the V data (Height x Width/2)
        } else if(colorSpace == 444){
            for(int i = 0; i < width * height; i++) U[i] = fgetc(input); //read the U data (Height x Width)
            for(int i = 0; i < width * height; i++) V[i] = fgetc(input); //read the V data (Height x Width)
        }
        

        //Create Mat objects for Y, U, and V
        Mat YMat = Mat(height, width, CV_8UC1);
        Mat UMat;
        Mat VMat;
        if(colorSpace == 420){
            UMat = Mat(height/2, width/2, CV_8UC1);
            VMat = Mat(height/2, width/2, CV_8UC1);
        } else if(colorSpace == 422){
            UMat = Mat(height, width/2, CV_8UC1);
            VMat = Mat(height, width/2, CV_8UC1);
        } else if(colorSpace == 444){
            UMat = Mat(height, width, CV_8UC1);
            VMat = Mat(height, width, CV_8UC1);
        }
        
        //copy the Y, U, and V data into the Mat objects
        //420 Color Space
        if(colorSpace == 420){
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++) YMat.at<uchar>(i, j) = Y[i * width + j];
                if (i < height/2 && i < width/2) {
                    for(int j = 0; j < width/2; j++){
                        UMat.at<uchar>(i, j) = U[i * width/2 + j];
                        VMat.at<uchar>(i, j) = V[i * width/2 + j];
                    }
                }
            }
        //422 Color Space
        } else if(colorSpace == 422){
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++) YMat.at<uchar>(i, j) = Y[i * width + j];
                for(int j = 0; j < width/2; j++){
                    UMat.at<uchar>(i, j) = U[i * width/2 + j];
                    VMat.at<uchar>(i, j) = V[i * width/2 + j];
                }
            }
        //444 Color Space
        } else if(colorSpace == 444){
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    YMat.at<uchar>(i, j) = Y[i * width + j];
                    UMat.at<uchar>(i, j) = U[i * width + j];
                    VMat.at<uchar>(i, j) = V[i * width + j];
                }
            }
        }
        //KEYFRAME SAVING
        //if its the first frame, set it as the keyframe and copy the data into the keyframe Mat objects
        if(frameIndex==0){
            keyYmat = YMat.clone();
            keyUmat = UMat.clone();
            keyVmat = VMat.clone();
        } //else if the current keyFrame is a multiple of keyFramePeriod, set the current frame as the keyframe and copy the data into the keyframe Mat objects
        else if(frameIndex % keyFramePeriod == 0){
            keyYmat = YMat.clone();
            keyUmat = UMat.clone();
            keyVmat = VMat.clone();
        }

        //PREDICTION 
        //INTRA-FRAME PREDICTION (keyFrame)
        //if its the first frame, or if the current frame is a keyframe, do not use inter frame prediction
        if(frameIndex==0 || (frameIndex % keyFramePeriod==0)){
            //go pixel by pixel through the Y, U, and V Mat objects to make predictions
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int Y = YMat.at<uchar>(i, j);
                    int U = UMat.at<uchar>(i, j);
                    int V = VMat.at<uchar>(i, j);
                    int Yerror = 0;
                    int Uerror = 0;
                    int Verror = 0;
                    //if its the first pixel of the image, do not use prediction
                    if (i == 0 && j == 0) {
                        Yerror = Y;
                        Uerror = U;
                        Verror = V;
                        Yresiduals.push_back(Yerror);
                        Cbresiduals.push_back(Uerror);
                        Crresiduals.push_back(Verror);
                    } else if(i==0){
                        //if its the first line of the image, use only the previous pixel (to the left)
                        Yerror = Y - YMat.at<uchar>(i, j-1);
                        Yresiduals.push_back(Yerror);
                        if(colorSpace == 420 || colorSpace == 422){
                            if (j < (width/2)) {
                                Cbresiduals.push_back(U - UMat.at<uchar>(i, j-1));
                                Crresiduals.push_back(V - VMat.at<uchar>(i, j-1));
                            }
                        } else if(colorSpace == 444){
                            Cbresiduals.push_back(U - UMat.at<uchar>(i, j-1));
                            Crresiduals.push_back(V - VMat.at<uchar>(i, j-1));
                        }
                    } else if(j==0){
                        //if its the first pixel of the line, use only the pixel above
                        Yerror = Y - YMat.at<uchar>(i-1, j);
                        Yresiduals.push_back(Yerror);
                        if (colorSpace == 420){
                            if (i < (height/2)) {
                                Cbresiduals.push_back(U - UMat.at<uchar>(i-1, j));
                                Crresiduals.push_back(V - VMat.at<uchar>(i-1, j));
                            }
                        } else {
                            Cbresiduals.push_back(U - UMat.at<uchar>(i-1, j));
                            Crresiduals.push_back(V - VMat.at<uchar>(i-1, j));
                        }
                    } else {
                        //otherwise, use the prediction function
                        Yerror = Y - predict(YMat.at<uchar>(i, j-1), YMat.at<uchar>(i-1, j), YMat.at<uchar>(i-1, j-1));
                        Yresiduals.push_back(Yerror);
                        if(colorSpace == 420){
                            if (i < (height/2) && j < (width/2)) {
                                Cbresiduals.push_back(U - predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1)));
                                Crresiduals.push_back(V - predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1)));
                            }
                        } else if(colorSpace == 422){
                            if (j < (width/2)) {
                                Cbresiduals.push_back(U - predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1)));
                                Crresiduals.push_back(V - predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1)));
                            }
                        } else if(colorSpace == 444){
                            Cbresiduals.push_back(U - predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1)));
                            Crresiduals.push_back(V - predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1)));
                        }
                    }
                }
            }
        } else {
            //INTER-FRAME PREDICTION (non-keyFrame, block by block)
            //motion compensation (block by block) with the keyframe
            //go block by block through the Y, U, and V Mat objects to make predictions
            //for each block calculate the motion vector

            //calculate the padding, check if width and height are divisible by block size
            int padded_width = width;
            int padded_height = height;
            if (width % blockSize != 0) {
                padded_width = width + (blockSize - (width % blockSize));
            }
            if (height % blockSize != 0) {
                padded_height = height + (blockSize - (height % blockSize));
            }

            //keyFrame Mat is saved in keyYmat, keyUmat, keyVmat

            //first thing to do is to create a Mat object for all of the frame, combining the Y, U and V Mat objects
            Mat frame = Mat(padded_height, padded_width, CV_8UC3);
            Mat keyFrameMat = Mat(padded_height, padded_width, CV_8UC3);
            //colorspace 420
            for (int i = 0; i < padded_height; i++){
                for (int j = 0; j < padded_width; j++){
                    int half_i = i/2;
                    int half_j = j/2;
                    frame.at<Vec3b>(i, j)[0] = YMat.at<uchar>(i, j);
                    frame.at<Vec3b>(i, j)[1] = UMat.at<uchar>(half_i, half_j);
                    frame.at<Vec3b>(i, j)[2] = VMat.at<uchar>(half_i, half_j);
                    keyFrameMat.at<Vec3b>(i, j)[0] = keyYmat.at<uchar>(i, j);
                    keyFrameMat.at<Vec3b>(i, j)[1] = keyUmat.at<uchar>(half_i, half_j);
                    keyFrameMat.at<Vec3b>(i, j)[2] = keyVmat.at<uchar>(half_i, half_j);
                }
            }
            
            

            //current Y, Cb and Cr block
            Mat frameBlock = Mat(blockSize, blockSize, CV_8UC3);

            int num_blocks_width = padded_width/blockSize;     //number of blocks on the width of the whole frame
            int num_blocks_height = padded_height/blockSize;   //number of blocks on the height of the whole frame
            // cout << num_blocks_height << " " << num_blocks_width << endl;
            int block_index = 0;    //index of the current block

            //motion vector calculation
            for (int bw = 0; bw < num_blocks_width; bw++){
                for(int bh = 0; bh < num_blocks_height; bh++){
                    //copy the pixels of the current frame 
                    for (int i = 0; i < blockSize; i++){
                        for (int j = 0; j < blockSize; j++){
                            frameBlock.at<Vec3b>(i, j)[0] = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[0];
                            frameBlock.at<Vec3b>(i, j)[1] = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[1];
                            frameBlock.at<Vec3b>(i, j)[2] = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[2];
                        }
                    }

                    //calculate the motion vector
                    int motionVectorX = 0;
                    int motionVectorY = 0;

                    //the area to search is the position of the block in the keyframe + searchDistance (in both directions - left, right, up and down)
                    //searchDistance is the number of pixels to search in each direction
                    //the first block is bw = 0, bh = 0, so the search area is from (0,0) to (blockSize + searchDistance, blockSize + searchDistance)
                    //the last block is bw = num_blocks_width - 1, bh = num_blocks_height - 1, so the search area is from (width - blockSize - searchDistance, height - blockSize - searchDistance) to (width - blockSize, height - blockSize)
                    int searchAreaTopX = bw*blockSize - searchDistance;
                    int searchAreaTopY = bh*blockSize - searchDistance;
                    int searchAreaBottomX = bw*blockSize + blockSize + searchDistance;
                    int searchAreaBottomY = bh*blockSize + blockSize + searchDistance;
                    if(searchAreaTopX < 0) searchAreaTopX = 0;
                    if(searchAreaTopY < 0) searchAreaTopY = 0;
                    if(searchAreaBottomX > width) searchAreaBottomX = width;
                    if(searchAreaBottomY > height) searchAreaBottomY = height;
                    //calculate the sum of the absolute differences between the current block and the blocks in the search area
                    int minSum = 1000000000;
                    for (int i = bh*blockSize - searchDistance; i <= bh*blockSize + searchDistance; i++){
                        for (int j = bw*blockSize - searchDistance; j <= bw*blockSize + searchDistance; j++){
                            if(i < 0 || j < 0 || i + blockSize > padded_height || j + blockSize > padded_width) continue;
                            Mat ref_block = keyFrameMat(Rect(j, i, blockSize, blockSize));
                            int sum = 0;
                            for (int k = 0; k < blockSize; k++){
                                for (int l = 0; l < blockSize; l++){
                                    sum += abs(frameBlock.at<uchar>(k,l) - ref_block.at<uchar>(k,l));
                                }
                            }
                            if (sum < minSum){
                                minSum = sum;
                                motionVectorX = j - bw*blockSize;
                                motionVectorY = i - bh*blockSize;
                            }
                        }
                    }
                    //save the motion vector
                    motionVectorXs.push_back(motionVectorX);
                    motionVectorYs.push_back(motionVectorY);
                    //END OF MOTION VECTOR CALCULATION

                    //predict the current block using the motion vector
                    for (int i = 0; i < blockSize; i++){ //height
                        for (int j = 0; j < blockSize; j++){ //width
                            int errorY = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[0] - keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorY, bw*blockSize + j + motionVectorX)[0];
                            int errorCb = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[1] - keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorY, bw*blockSize + j + motionVectorX)[1];
                            int errorCr = frame.at<Vec3b>(bh*blockSize + i, bw*blockSize + j)[2] - keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorY, bw*blockSize + j + motionVectorX)[2];
                            Yresiduals.push_back(errorY);
                            //only save the Cb and Cr residuals every 2 pixels 
                            if (i % 2 == 0 and j % 2 == 0){
                                Cbresiduals.push_back(errorCb);
                                Crresiduals.push_back(errorCr);
                            }
                        }
                    }
                    block_index++;
                }
            }
        }
        //M VECTOR CALCULATION
        for (long unsigned int i = 0; i < Yresiduals.size(); i++) {
            if (i % blockSize == 0 and i != 0) {
                int sum = 0;
                for (long unsigned int j = i - blockSize; j < i; j++) sum += abs(Yresiduals[j]);
                int mean = sum / blockSize;
                int m = calc_m(mean);
                if (m == 0) m = 1;
                Ym_vector.push_back(m);
                if (i < Cbresiduals.size()) {
                    int sumCb = 0;
                    int sumCr = 0;
                    for (long unsigned int j = i - blockSize; j < i; j++) {
                        sumCb += abs(Cbresiduals[j]);
                        sumCr += abs(Crresiduals[j]);
                    }
                    int meanCb = sumCb / blockSize;
                    int meanCr = sumCr / blockSize;
                    int mCb = calc_m(meanCb);
                    int mCr = calc_m(meanCr);
                    if (mCb == 0) mCb = 1;
                    if (mCr == 0) mCr = 1;
                    Cbm_vector.push_back(mCb);
                    Crm_vector.push_back(mCr);
                }
            }
            if (i == Yresiduals.size() - 1) {
                int sum = 0;
                for (long unsigned int j = i - (i % blockSize); j < i; j++) sum += abs(Yresiduals[j]);
                
                int mean = sum / (i % blockSize);
                int m = calc_m(mean);
                if (m == 0) m = 1;
                Ym_vector.push_back(m);
            }

            if (i == Cbresiduals.size() - 1) {
                int sumCb = 0;
                int sumCr = 0;
                for (long unsigned int j = i - (i % blockSize); j < i; j++) {
                    sumCb += abs(Cbresiduals[j]);
                    sumCr += abs(Crresiduals[j]);
                }
                int meanCb = sumCb / (i % blockSize);
                int meanCr = sumCr / (i % blockSize);
                int mCb = calc_m(meanCb);
                int mCr = calc_m(meanCr);
                if (mCb == 0) mCb = 1;
                if (mCr == 0) mCr = 1;
                Cbm_vector.push_back(mCb);
                Crm_vector.push_back(mCr);
            }
        }

        //Quantization and encoding
        Golomb g;
        int m_index = 0;
        for (long unsigned int i = 0; i < Yresiduals.size(); i++) {
            if (i % blockSize == 0 and i != 0) {
                Ym.push_back(Ym_vector[m_index]);
                m_index++;
            }
            //only quantize the residuals that are not in the keyframe
            if((frameIndex !=0) && (frameIndex % keyFramePeriod != 0)){
                Yresiduals[i] = Yresiduals[i] >> quantization;
            }
            Yencoded += g.encode(Yresiduals[i], Ym_vector[m_index]);
            if (i == Yresiduals.size() - 1) {
                Ym.push_back(Ym_vector[m_index]);
            }  
        }
        m_index = 0;
        for (long unsigned int i = 0; i < Cbresiduals.size(); i++) {
            if (i % blockSize == 0 and i != 0) {
                Cbm.push_back(Cbm_vector[m_index]);
                Crm.push_back(Crm_vector[m_index]);
                m_index++;
            }

            Cbencoded += g.encode(Cbresiduals[i], Cbm_vector[m_index]);
            Crencoded += g.encode(Crresiduals[i], Crm_vector[m_index]);
            if (i == Cbresiduals.size() - 1) {
                Cbm.push_back(Cbm_vector[m_index]);
                Crm.push_back(Crm_vector[m_index]);
            }
        }
        Ym_vector = vector<int>();
        Cbm_vector = vector<int>();
        Crm_vector = vector<int>();
        totalYresiduals += Yresiduals.size();
        totalCbresiduals += Cbresiduals.size();
        totalCrresiduals += Crresiduals.size();
        Yresiduals = vector<int>();
        Cbresiduals = vector<int>();
        Crresiduals = vector<int>();

        for (long unsigned int i = 0; i < Yencoded.length(); i++)
            encoded_Ybits.push_back(Yencoded[i] - '0');
        for (long unsigned int i = 0; i < Cbencoded.length(); i++)
            encoded_Cbbits.push_back(Cbencoded[i] - '0');
        for (long unsigned int i = 0; i < Crencoded.length(); i++)
            encoded_Crbits.push_back(Crencoded[i] - '0');

        Yencoded = "";
        Cbencoded = "";
        Crencoded = "";
        frameIndex++;
        // if (numFrames == 4) break;
    } //end of loop for each frame


    string motionXencoded = "";
    string motionYencoded = "";
    Golomb g;
    for(long unsigned int i = 0; i < motionVectorXs.size(); i++) motionXencoded += g.encode(motionVectorXs[i], 8);
    for(long unsigned int i = 0; i < motionVectorYs.size(); i++) motionYencoded += g.encode(motionVectorYs[i], 8);
    vector<int> encoded_motionXbits;
    vector<int> encoded_motionYbits;
    for (long unsigned int i = 0; i < motionXencoded.length(); i++)
        encoded_motionXbits.push_back(motionXencoded[i] - '0');
    for (long unsigned int i = 0; i < motionYencoded.length(); i++)
        encoded_motionYbits.push_back(motionYencoded[i] - '0');

    clock_t end2 = clock();
    double elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to read, predict and encode YUV values (and m) from frame: " << elapsed_secs2 << " ms" << endl;

    BitStream bs(argv[2], "w");
    vector<int> bits;

    start2 = clock();

    for (int i = 15; i >= 0; i--) bits.push_back((width >> i) & 1);                     //the first 16 bits are the width of the image
    for (int i = 15; i >= 0; i--) bits.push_back((height >> i) & 1);                    //the next 16 bits are the height of the image
    for (int i = 15; i >= 0; i--) bits.push_back((numFrames >> i) & 1);                 //the next 16 bits are the number of frames
    for (int i = 15; i >= 0; i--) bits.push_back((colorSpace >> i) & 1);                //the next 16 bits are the color space
    for (int i = 15; i >= 0; i--) bits.push_back((aspectRatio1 >> i) & 1);              //the next 16 bits are aspect ratio 1
    for (int i = 15; i >= 0; i--) bits.push_back((aspectRatio2 >> i) & 1);              //the next 16 bits are aspect ratio 2
    for (int i = 15; i >= 0; i--) bits.push_back((frameRate1 >> i) & 1);                //the next 16 bits are the frame rate 1
    for (int i = 15; i >= 0; i--) bits.push_back((frameRate2 >> i) & 1);                //the next 16 bits are the frame rate 2
    for (int i = 0;  i <  8; i++) bits.push_back(interlace_v[i]);                       //the next 8 bits are the interlace vector el
    for (int i = 15;  i >= 0; i--) bits.push_back((blockSize >> i) & 1);                 //the next 8 bits are the block size
    for (int i = 15; i >= 0; i--) bits.push_back((searchDistance >> i) & 1);            //the next 16 bits are the searchDistance
    for (int i = 15; i >= 0; i--) bits.push_back((quantization >> i) & 1);              //the next 16 bits are the quantization
    for (int i = 15; i >= 0; i--) bits.push_back((keyFramePeriod >> i) & 1);            //the next 16 bits are the keyFramePeriod
    for (int i = 15; i >= 0; i--) bits.push_back((padded_width >> i) & 1);              //the next 16 bits are the padded_width
    for (int i = 15; i >= 0; i--) bits.push_back((padded_height >> i) & 1);             //the next 16 bits are the padded_height
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Ybits.size() >> i) & 1);      //the next 32 bits are the encoded_Ybits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Cbbits.size() >> i) & 1);     //the next 32 bits are the encoded_Cbbits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Crbits.size() >> i) & 1);     //the next 32 bits are the encoded_Crbits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Ym.size() >> i) & 1);                 //the next 32 bits are the Ym.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Cbm.size() >> i) & 1);                //the next 32 bits are the Cbm.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Crm.size() >> i) & 1);                //the next 32 bits are the Crm.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_motionXbits.size() >> i) & 1);     //the next 32 bits are the motionVectorXs.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_motionYbits.size() >> i) & 1);     //the next 32 bits are the motionVectorYs.size()
    for (long unsigned int i = 0; i < encoded_motionXbits.size(); i++) bits.push_back(encoded_motionXbits[i]); //the next bits are the encoded_motionXbits
    for (long unsigned int i = 0; i < encoded_motionYbits.size(); i++) bits.push_back(encoded_motionYbits[i]); //the next bits are the encoded_motionYbits
    for (long unsigned int i = 0; i < Ym.size(); i++) {                                 //the next bits are the Ym values
        for (int j = 7; j >= 0; j--) bits.push_back((Ym[i] >> j) & 1);                  //the next 8 bits are the Ym value
    }
    for (long unsigned int i = 0; i < Cbm.size(); i++) {                                //the next bits are the Cbm values
        for (int j = 7; j >= 0; j--) bits.push_back((Cbm[i] >> j) & 1);                 //the next 8 bits are the Cbm value
    }
    for (long unsigned int i = 0; i < Crm.size(); i++) {                                //the next bits are the Crm values
        for (int j = 7; j >= 0; j--) bits.push_back((Crm[i] >> j) & 1);                 //the next 8 bits are the Crm value
    }
    for(long unsigned int i = 0; i < encoded_Ybits.size(); i++) bits.push_back(encoded_Ybits[i]);   //the next bits are the encoded_Ybits
    for(long unsigned int i = 0; i < encoded_Cbbits.size(); i++) bits.push_back(encoded_Cbbits[i]); //the next bits are the encoded_Cbbits
    for(long unsigned int i = 0; i < encoded_Crbits.size(); i++) bits.push_back(encoded_Crbits[i]); //the next bits are the encoded_Crbits

    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to push back all values to bits: " << elapsed_secs2 << " ms" << endl;
    start2 = clock();
    
    bs.writeBits(bits);
    bs.close();
    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to write to bits: " << elapsed_secs2 << " ms" << endl;

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    elapsed_secs = elapsed_secs * 1000;
    cout << "Execution time: " << elapsed_secs << " ms" << endl;
    return 0;
}
