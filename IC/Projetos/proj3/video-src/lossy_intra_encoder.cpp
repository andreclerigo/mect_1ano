#include "golomb.h"
#include "bitStream.h"
#include "opencv2/opencv.hpp"
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
    if(argc != 5){
        cout << "Usage: " << argv[0] << " <input file> <output file> <block size> <quantization>" << endl;
        return 1;
    }

    //Open input file
    FILE* input = fopen(argv[1], "r");
    if(input == NULL){
        cout << "Error: Could not open input file" << endl;
        return 1;
    }

    int blockSize = atoi(argv[3]);

    YUV4MPEG2Reader reader(argv[1]);

    int width = reader.width();

    int quantization = atoi(argv[4]);
    if (quantization < 1 or quantization > 16) {
        cout << "Error: Quantization must be greater than 0 and lower than 16!" << endl;
        return 1;
    }
    // print quantization
    cout << "Quantization: " << quantization << endl;
    // Warn that since the block size is greater than the width it will be used the image's width
    if(blockSize > width){
        cout << "Warning: Since block size is greater than the width it will be used the image's width!" << endl;
        blockSize = width;
    }

    int height = reader.height();
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

    vector<int> Y, U, V;
    string Yencoded = "";
    string Cbencoded = "";
    string Crencoded = "";
    vector<short> Ym, Cbm, Crm;
    vector<int> encoded_Ybits, encoded_Cbbits, encoded_Crbits;

    //start a timer
    clock_t start = clock();

    vector<int> Ym_vector;
    vector<int> Cbm_vector;
    vector<int> Crm_vector;
    vector<int> Yresiduals;
    vector<int> Cbresiduals;
    vector<int> Crresiduals;
    
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

    while(!feof(input)){
        numFrames++;
        //read the frame line
        fgets(line, 100, input);
        //read the Y data  (Height x Width)
        for(int i = 0; i < width * height; i++){
            Y[i] = fgetc(input);  
            // if(numFrames == 1 and i < 1000)  cout << Y[i] << endl;
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
        //go pixel by pixel through the Y, U, and V Mat objects to make predictions
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int Y = YMat.at<uchar>(i, j);
                int U = UMat.at<uchar>(i, j);
                int V = VMat.at<uchar>(i, j);
                //if its the first pixel of the image, do not use prediction
                if (i == 0 && j == 0) {
                    Yresiduals.push_back(Y);
                    Cbresiduals.push_back(U);
                    Crresiduals.push_back(V);
                } else if(i==0){
                    //if its the first line of the image, use only the previous pixel (to the left)
                    Yresiduals.push_back(Y - YMat.at<uchar>(i, j-1));
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
                    Yresiduals.push_back(Y - YMat.at<uchar>(i-1, j));
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
                    Yresiduals.push_back(Y - predict(YMat.at<uchar>(i, j-1), YMat.at<uchar>(i-1, j), YMat.at<uchar>(i-1, j-1)));
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

        //PADDING TO VECTORS
        if (Yresiduals.size() % blockSize != 0) {
            // cout << "Padding Yresiduals" << endl;
            int padding = blockSize - (Yresiduals.size() % blockSize);
            for (int i = 0; i < padding; i++) Yresiduals.push_back(0);
        }
        if (Cbresiduals.size() % blockSize != 0) {
            int padding = blockSize - (Cbresiduals.size() % blockSize);
            // cout << "Padding Cbresiduals" << endl;
            for (int i = 0; i < padding; i++) {
                Cbresiduals.push_back(0);
                Crresiduals.push_back(0);
            }
        }

        // Quantization of residuals
        for (long unsigned int i = 0; i < Yresiduals.size(); i++) {
            Yresiduals[i] = Yresiduals[i] >> quantization;
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

        Golomb g;
        int m_index = 0;
        for (long unsigned int i = 0; i < Yresiduals.size(); i++) {
            if (i % blockSize == 0 and i != 0) {
                Ym.push_back(Ym_vector[m_index]);
                m_index++;
            }
            Yencoded += g.encode(Yresiduals[i], Ym_vector[m_index]);
            if (i == Yresiduals.size() - 1) Ym.push_back(Ym_vector[m_index]);
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
        // if (numFrames == 4) break;
    } //end of loop for each frame

    BitStream bs(argv[2], "w");
    vector<int> bits;

    for (int i = 15; i >= 0; i--) bits.push_back((width >> i) & 1);         //the first 16 bits are the width of the image
    for (int i = 15; i >= 0; i--) bits.push_back((height >> i) & 1);        //the next 16 bits are the height of the image
    for (int i = 15; i >= 0; i--) bits.push_back((numFrames >> i) & 1);     //the next 16 bits are the number of frames
    for (int i = 15; i >= 0; i--) bits.push_back((colorSpace >> i) & 1);    //the next 16 bits are the color space
    for (int i = 15; i >= 0; i--) bits.push_back((aspectRatio1 >> i) & 1);  //the next 16 bits are aspect ratio 1
    for (int i = 15; i >= 0; i--) bits.push_back((aspectRatio2 >> i) & 1);  //the next 16 bits are aspect ratio 2
    for (int i = 15; i >= 0; i--) bits.push_back((frameRate1 >> i) & 1);    //the next 16 bits are the frame rate 1
    for (int i = 15; i >= 0; i--) bits.push_back((frameRate2 >> i) & 1);    //the next 16 bits are the frame rate 2
    for (int i = 0;  i <  8; i++) bits.push_back(interlace_v[i]);           //the next 8 bits are the interlace vector elements
    for (int i = 7; i >= 0; i--) bits.push_back((quantization >> i) & 1);   //the next 8 bits are the quantization
    for (int i = 15;  i >= 0; i--) bits.push_back((blockSize >> i) & 1);     //the next 16 bits are the block size
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Ybits.size() >> i) & 1);  //the next 32 bits are the encoded_Ybits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Cbbits.size() >> i) & 1); //the next 32 bits are the encoded_Cbbits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((encoded_Crbits.size() >> i) & 1); //the next 32 bits are the encoded_Crbits.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Ym.size() >> i) & 1);     //the next 32 bits are the Ym.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Cbm.size() >> i) & 1);    //the next 32 bits are the Cbm.size()
    for (int i = 31; i >= 0; i--) bits.push_back((Crm.size() >> i) & 1);    //the next 32 bits are the Crm.size()
    for (long unsigned int i = 0; i < Ym.size(); i++) {                     //the next bits are the Ym values
        for (int j = 7; j >= 0; j--) bits.push_back((Ym[i] >> j) & 1);      //the next 8 bits are the Ym value
    }
    for (long unsigned int i = 0; i < Cbm.size(); i++) {                    //the next bits are the Cbm values
        for (int j = 7; j >= 0; j--) bits.push_back((Cbm[i] >> j) & 1);     //the next 8 bits are the Cbm value
    }
    for (long unsigned int i = 0; i < Crm.size(); i++) {                    //the next bits are the Crm values
        for (int j = 7; j >= 0; j--) bits.push_back((Crm[i] >> j) & 1);     //the next 8 bits are the Crm value
    }
    for(long unsigned int i = 0; i < encoded_Ybits.size(); i++) bits.push_back(encoded_Ybits[i]);   //the next bits are the encoded_Ybits
    for(long unsigned int i = 0; i < encoded_Cbbits.size(); i++) bits.push_back(encoded_Cbbits[i]); //the next bits are the encoded_Cbbits
    for(long unsigned int i = 0; i < encoded_Crbits.size(); i++) bits.push_back(encoded_Crbits[i]); //the next bits are the encoded_Crbits
    
    bs.writeBits(bits);
    bs.close();
   
    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    elapsed_secs = elapsed_secs * 1000;
    cout << "Execution time: " << elapsed_secs << " ms" << endl;
    return 0;
}
