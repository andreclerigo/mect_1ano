#include "bitStream.h"
#include "opencv2/opencv.hpp"
#include <vector>
#include <string>
#include <string.h>
#include <iostream>
#include "YUV4MPEG2Reader.h"
#include "golomb.h"

using namespace std;
using namespace cv;

int main(int argc, char* argv[]){
    //function that converts array of ints (1 or 0) to int
    auto bits_to_int = [](vector<int> bits) {
        int c = 0;
        for(long unsigned int i = 0; i < bits.size(); i++) c += bits[i] * pow(2, bits.size() - i - 1);
        return c;
    };

    //function that converts array of bits to char
    auto bits_to_char = [](vector<int> bits) {
        int c = 0;
        for(long unsigned int i = 0; i < bits.size(); i++) c += bits[i] * pow(2, bits.size() - i - 1);
        return c;
    };

    auto predict = [](int a, int b, int c) {
        if (c >= max(a, b))
            return min(a, b);   //min(left, above) if  left top >= max(left, above)
        else if (c <= min(a, b))
            return max(a, b);   //max(left, above) if  left top <= min(left, above)
        else
            return a + b - c;   //otherwise, left + above - left top
    };

    //start a timer
    clock_t start = clock();

    if(argc != 3){
        cout << "Usage: " << argv[0] << " <input file> <output file>" << endl;
        return 1;
    }
    string input_file = argv[1];
    BitStream bs(input_file, "r");
    string output_file = argv[2];

    //The output file is a YUV4MPEG2 file
    //write the header
    ofstream out(output_file, ios::out | ios::binary);

    //start a new timer
    clock_t start2 = clock();
    vector<int> v_width = bs.readBits(16);
    vector<int> v_height = bs.readBits(16);
    vector<int> v_num_frames = bs.readBits(16);
    vector<int> v_color_space = bs.readBits(16);
    vector<int> v_aspect_ratio_1 = bs.readBits(16);
    vector<int> v_aspect_ratio_2 = bs.readBits(16);
    vector<int> v_frame_rate_1 = bs.readBits(16);
    vector<int> v_frame_rate_2 = bs.readBits(16);
    vector<int> v_interlace = bs.readBits(8);
    vector<int> v_bs = bs.readBits(16);
    vector<int> v_searchD = bs.readBits(16);
    vector<int> v_quant = bs.readBits(16);
    vector<int> v_keyFrame = bs.readBits(16);
    vector<int> v_paddedWidth = bs.readBits(16);
    vector<int> v_paddedHeight = bs.readBits(16);
    vector<int> v_Ybits_size = bs.readBits(32);
    vector<int> v_Cbbits_size = bs.readBits(32);
    vector<int> v_Crbits_size = bs.readBits(32);
    vector<int> v_Ym_size = bs.readBits(32);
    vector<int> v_Cbm_size = bs.readBits(32);
    vector<int> v_Crm_size = bs.readBits(32);
    vector<int> v_motionXSize = bs.readBits(32);
    vector<int> v_motionYSize = bs.readBits(32);

    int width = bits_to_int(v_width);
    int height = bits_to_int(v_height);
    int num_frames = bits_to_int(v_num_frames);
    int color_space = bits_to_int(v_color_space);
    int aspect_ratio_1 = bits_to_int(v_aspect_ratio_1);
    int aspect_ratio_2 = bits_to_int(v_aspect_ratio_2);
    int frame_rate_1 = bits_to_int(v_frame_rate_1);
    int frame_rate_2 = bits_to_int(v_frame_rate_2);
    char interlace = bits_to_char(v_interlace);
    int blockSize = bits_to_int(v_bs);
    int quantization = bits_to_int(v_quant);
    int keyFramePeriod = bits_to_int(v_keyFrame);
    int padded_width = bits_to_int(v_paddedWidth);
    int padded_height = bits_to_int(v_paddedHeight);
    int Ybits_size = bits_to_int(v_Ybits_size);
    int Cbbits_size = bits_to_int(v_Cbbits_size);
    int Crbits_size = bits_to_int(v_Crbits_size);
    int Ym_size = bits_to_int(v_Ym_size);
    int Cbm_size = bits_to_int(v_Cbm_size);
    int Crm_size = bits_to_int(v_Crm_size);
    int motionXSize = bits_to_int(v_motionXSize);
    int motionYSize = bits_to_int(v_motionYSize);

    //write the header
    if(color_space != 420)
        out << "YUV4MPEG2 W" << width << " H" << height << " F" << frame_rate_1 << ":" << frame_rate_2 << " I" << interlace << " A" << aspect_ratio_1 << ":" << aspect_ratio_2 << " C" << color_space << endl;
    else 
        out << "YUV4MPEG2 W" << width << " H" << height << " F" << frame_rate_1 << ":" << frame_rate_2 << " I" << interlace << " A" << aspect_ratio_1 << ":" << aspect_ratio_2 << endl;

    //write to the file FRAME
    out << "FRAME" << endl;

    //READING MOTION VECTORS
    vector<int> motionX = bs.readBits(motionXSize);
    vector<int> motionY = bs.readBits(motionYSize);
    string motionX_str = "";
    string motionY_str = "";
    for (long unsigned int i = 0; i < motionX.size(); i++) motionX_str += motionX[i] + '0';
    for (long unsigned int i = 0; i < motionY.size(); i++) motionY_str += motionY[i] + '0';
    Golomb gMotion;
    vector<int> motionVectorXs = gMotion.decode(motionX_str, 8);
    vector<int> motionVectorYs = gMotion.decode(motionY_str, 8);

    //READING M VALUES
    vector<int> Ym;
    for(int i = 0; i < Ym_size; i++){
        vector<int> v_Ym = bs.readBits(8);
        int Ym_i = 0;
        for (long unsigned int j = 0; j < v_Ym.size(); j++) Ym_i += v_Ym[j] * pow(2, v_Ym.size() - j - 1);
        Ym.push_back(Ym_i);
    }

    vector<int> Cbm;
    for(int i = 0; i < Cbm_size; i++){
        vector<int> v_Cbm = bs.readBits(8);
        int Cbm_i = 0;
        for (long unsigned int j = 0; j < v_Cbm.size(); j++) Cbm_i += v_Cbm[j] * pow(2, v_Cbm.size() - j - 1);
        Cbm.push_back(Cbm_i);
    }

    vector<int> Crm;
    for(int i = 0; i < Crm_size; i++){
        vector<int> v_Crm = bs.readBits(8);
        int Crm_i = 0;
        for (long unsigned int j = 0; j < v_Crm.size(); j++) Crm_i += v_Crm[j] * pow(2, v_Crm.size() - j - 1);
        Crm.push_back(Crm_i);
    }
    //end the timer
    clock_t end2 = clock();
    double elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to read header including m: " << elapsed_secs2 << " ms" << endl;

    //start the timer
    start2 = clock();

    //READING YUV VALUES
    vector<int> Ybits = bs.readBits(Ybits_size);
    vector<int> Cbbits = bs.readBits(Cbbits_size);
    vector<int> Crbits = bs.readBits(Crbits_size);

    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to read including YUV values: " << elapsed_secs2 << " ms" << endl;

    //start the timer
    start2 = clock();

    string Yencodedstring = "";
    for(long unsigned int i = 0; i < Ybits.size(); i++) Yencodedstring += Ybits[i] + '0';
    string Cbencodedstring = "";
    string Crencodedstring = "";
    for(long unsigned int i = 0; i < Cbbits.size(); i++) {
        Cbencodedstring += Cbbits[i] + '0';
    }
    for(long unsigned int i = 0; i < Crbits.size(); i++) {
        Crencodedstring += Crbits[i] + '0';
    }

    //end the timer
    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to parse YUV values: " << elapsed_secs2 << " ms" << endl;
    //start the timer
    start2 = clock();

    //DECODE YUV VALUES
    Golomb g;
    vector<int> Ydecoded = g.decodeMultiple(Yencodedstring, Ym, blockSize);
    vector<int> Cbdecoded = g.decodeMultiple(Cbencodedstring, Cbm, blockSize);
    vector<int> Crdecoded = g.decodeMultiple(Crencodedstring, Crm, blockSize);

    //undo the quantization of YUV decoded values back to the original size
    int frameIndex = 0;
    int total = padded_height*padded_width;

    for (long unsigned int i = 0; i < Ydecoded.size(); i++){
        if (i % total == 0 and i != 0) {
                frameIndex++;
        }
        //Only undo quantization if it is not a keyframe
        if((frameIndex !=0) && (frameIndex % keyFramePeriod != 0)){
            if(quantization != 1){
            
                Ydecoded[i] = Ydecoded[i] << 1;
                Ydecoded[i] = Ydecoded[i] | 1;
                Ydecoded[i] = Ydecoded[i] << (quantization - 1);
            }else{
                Ydecoded[i] = Ydecoded[i] << 1;
            }
        }
    }

    Mat YMat = Mat(height, width, CV_8UC1);
    Mat UMat;
    Mat VMat;

    if (color_space == 420) {
        UMat = Mat(height/2, width/2, CV_8UC1);
        VMat = Mat(height/2, width/2, CV_8UC1);
    } else if (color_space == 422) {
        UMat = Mat(height, width/2, CV_8UC1);
        VMat = Mat(height, width/2, CV_8UC1);
    } else if (color_space == 444) {
        UMat = Mat(height, width, CV_8UC1);
        VMat = Mat(height, width, CV_8UC1);
    }

    //end the timer
    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to decode YUV values: " << elapsed_secs2 << " ms" << endl;

    //start the timer
    start2 = clock();

    //undo the predictions
    int pixel_idx = 0;
    int pixel_idx2 = 0;

    Mat keyYmat, keyUmat, keyVmat;
    int motionX_idx = 0;
    int motionY_idx = 0;
    //FOR EACH FRAME
    for (int n = 0; n < num_frames; n++) {

        //PREDICTION 
        //INTRA-FRAME PREDICTION (keyFrame)
        //if its the first frame, or if the current frame is a keyframe, do not use inter frame prediction
        if(n == 0 || (n % keyFramePeriod == 0)){
            if (color_space == 420) {
                UMat = Mat(height/2, width/2, CV_8UC1);
                VMat = Mat(height/2, width/2, CV_8UC1);
            } else if (color_space == 422) {
                UMat = Mat(height, width/2, CV_8UC1);
                VMat = Mat(height, width/2, CV_8UC1);
            } else if (color_space == 444) {
                UMat = Mat(height, width, CV_8UC1);
                VMat = Mat(height, width, CV_8UC1);
            }

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (i == 0 && j == 0) {
                        YMat.at<uchar>(i,j) = Ydecoded[pixel_idx];
                        UMat.at<uchar>(i,j) = Cbdecoded[pixel_idx2];
                        VMat.at<uchar>(i,j) = Crdecoded[pixel_idx2];
                        pixel_idx2++;
                    } else if (i == 0) {
                        int value = Ydecoded[pixel_idx] + YMat.at<uchar>(i, j-1);
                        if (value < 0) value = 0;
                        if (value > 255) value = 255;
                        YMat.at<uchar>(i, j) = value;
                        if (color_space == 420 || color_space == 422) {
                            if (j < (width/2)) {
                                int value = Cbdecoded[pixel_idx2] + UMat.at<uchar>(i, j-1);
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                UMat.at<uchar>(i,j) = value;
                                value = Crdecoded[pixel_idx2] + VMat.at<uchar>(i, j-1);
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                VMat.at<uchar>(i,j) = value;
                                pixel_idx2++;
                            }
                        } else if (color_space == 444) {
                            UMat.at<uchar>(i,j) = Cbdecoded[pixel_idx2] + UMat.at<uchar>(i, j-1);
                            VMat.at<uchar>(i,j) = Crdecoded[pixel_idx2] + VMat.at<uchar>(i, j-1);
                            pixel_idx2++;
                        }
                    } else if (j == 0) {
                        int value = Ydecoded[pixel_idx] + YMat.at<uchar>(i-1, j);
                        if (value < 0) value = 0;
                        if (value > 255) value = 255;
                        YMat.at<uchar>(i, j) = value;
                        if (color_space == 420) {
                            if (i < (height/2)) {
                                int value = Cbdecoded[pixel_idx2] + UMat.at<uchar>(i-1, j);
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                UMat.at<uchar>(i,j) = value;
                                value = Crdecoded[pixel_idx2] + VMat.at<uchar>(i-1, j);
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                VMat.at<uchar>(i,j) = value;
                                pixel_idx2++;
                            }
                        } else if(color_space == 422 || color_space == 444){
                            UMat.at<uchar>(i,j) = Cbdecoded[pixel_idx2] + UMat.at<uchar>(i-1, j);
                            VMat.at<uchar>(i,j) = Crdecoded[pixel_idx2] + VMat.at<uchar>(i-1, j);
                            pixel_idx2++;
                        }
                    } else {
                        int value = Ydecoded[pixel_idx] + predict(YMat.at<uchar>(i, j-1), YMat.at<uchar>(i-1, j), YMat.at<uchar>(i-1, j-1));
                        if (value < 0) value = 0;
                        if (value > 255) value = 255;
                        YMat.at<uchar>(i, j) = value;
                        if(color_space == 420){
                            if(i < (height/2) && j < (width/2)){
                                int value = Cbdecoded[pixel_idx2] + predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1));
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                UMat.at<uchar>(i,j) = value;
                                value = Crdecoded[pixel_idx2] + predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1));
                                if (value < 0) value = 0;
                                if (value > 255) value = 255;
                                VMat.at<uchar>(i,j) = value;
                                pixel_idx2++;
                            }
                        } else if(color_space == 422){
                            if(j < (width/2)){
                                UMat.at<uchar>(i,j) = Cbdecoded[pixel_idx2] + predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1));
                                VMat.at<uchar>(i,j) = Crdecoded[pixel_idx2] + predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1));
                                pixel_idx2++;
                            }
                        } else if(color_space == 444){
                            UMat.at<uchar>(i,j) = Cbdecoded[pixel_idx2] + predict(UMat.at<uchar>(i, j-1), UMat.at<uchar>(i-1, j), UMat.at<uchar>(i-1, j-1));
                            VMat.at<uchar>(i,j) = Crdecoded[pixel_idx2] + predict(VMat.at<uchar>(i, j-1), VMat.at<uchar>(i-1, j), VMat.at<uchar>(i-1, j-1));
                            pixel_idx2++;
                        }
                    }
                    pixel_idx++;
                }
            }
            //KEYFRAME SAVING
            keyYmat = YMat.clone();
            keyUmat = UMat.clone();
            keyVmat = VMat.clone();


            
            //convert the matrix back to a vector
            vector<int> Y_vector;
            vector<int> Cb_vector;
            vector<int> Cr_vector;
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    Y_vector.push_back(YMat.at<uchar>(i, j));
                }
                if(color_space == 420){
                    if (i < height/2 && i < width/2) {
                        for(int j = 0; j < width/2; j++){
                            Cb_vector.push_back(UMat.at<uchar>(i, j));
                            Cr_vector.push_back(VMat.at<uchar>(i, j));
                        }
                    }
                } else if(color_space == 422){
                    if (i < width/2) {
                        for(int j = 0; j < width/2; j++){
                            Cb_vector.push_back(UMat.at<uchar>(i, j));
                            Cr_vector.push_back(VMat.at<uchar>(i, j));
                        }
                    }
                } else if(color_space == 444){
                    for(int j = 0; j < width; j++){
                        Cb_vector.push_back(UMat.at<uchar>(i, j));
                        Cr_vector.push_back(VMat.at<uchar>(i, j));
                    }
                }
            }

            //write the Y_vector to the file
            for(long unsigned int i = 0; i < Y_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Y_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }

            //write the Cb_vector to the file
            for(long unsigned int i = 0; i < Cb_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Cb_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }

            //write the Cr_vector to the file
            for(long unsigned int i = 0; i < Cr_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Cr_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }
        } else {
            //INTER-FRAME PREDICTION (non-keyFrame, block by block)
            //motion compensation (block by block) with the keyframe
            //go block by block through the Y, U, and V Mat object to make predictions
            YMat = Mat(padded_height, padded_width, CV_8UC1);
            UMat = Mat(padded_height/2, padded_width/2, CV_8UC1);
            VMat = Mat(padded_height/2, padded_width/2, CV_8UC1);
            // //create a bidimensional vector to store the blocks
            // vector<vector<int>> YBi = vector<vector<int>>(padded_height, vector<int>(padded_width));
            // vector<vector<int>> UBi;
            // vector<vector<int>> VBi;
            // if (color_space == 420) {
            //     UMat = Mat(padded_height/2, padded_width/2, CV_8UC1);
            //     VMat = Mat(padded_height/2, padded_width/2, CV_8UC1);
            //     UBi = vector<vector<int>>(padded_height/2, vector<int>(padded_width/2));
            //     VBi = vector<vector<int>>(padded_height/2, vector<int>(padded_width/2));
            // }
            // for (int i = 0; i < padded_height; i++){
            //     for (int j = 0; j < padded_width; j++){

            //         YMat.at<uchar>(i, j) = Ydecoded[pixel_idx];
            //         YBi[i][j] = Ydecoded[pixel_idx];
            //         if ((i < padded_height/2) && (j < padded_width/2)) {
            //             UMat.at<uchar>(i, j) = Cbdecoded[pixel_idx2];
            //             VMat.at<uchar>(i, j) = Crdecoded[pixel_idx2];
            //             UBi[i][j] = Cbdecoded[pixel_idx2];
            //             VBi[i][j] = Crdecoded[pixel_idx2];
            //             pixel_idx2++;
            //         }
            //         pixel_idx++;
            //     }
            // }
            //FRAME CONSTRUCTION
            Mat frame = Mat(padded_height, padded_width, CV_8UC3);
            vector<vector<int>> frameBiY = vector<vector<int>>(padded_height, vector<int>(padded_width));
            vector<vector<int>> frameBiU = vector<vector<int>>(padded_height, vector<int>(padded_width));
            vector<vector<int>> frameBiV = vector<vector<int>>(padded_height, vector<int>(padded_width));
            Mat keyFrameMat = Mat(padded_height, padded_width, CV_8UC3);
            //colorspace 420
            for (int i = 0; i < padded_height; i++){
                for (int j = 0; j < padded_width; j++){
                    int half_i = i/2;
                    int half_j = j/2;
                    // frame.at<Vec3b>(i, j)[0] = YMat.at<uchar>(i, j);
                    // frame.at<Vec3b>(i, j)[1] = UMat.at<uchar>(half_i, half_j);
                    // frame.at<Vec3b>(i, j)[2] = VMat.at<uchar>(half_i, half_j);

                    //after blockSize block_j iterations, increment block_i and reset block_j
                    //after blockSize block_i iterations, reset block_i and block_j
                    // frameBiY[i][j] = YBi[i][j];
                    // frameBiU[i][j] = UBi[half_i][half_j];
                    // frameBiV[i][j] = VBi[half_i][half_j];

                    keyFrameMat.at<Vec3b>(i, j)[0] = keyYmat.at<uchar>(i, j);
                    keyFrameMat.at<Vec3b>(i, j)[1] = keyUmat.at<uchar>(half_i, half_j);
                    keyFrameMat.at<Vec3b>(i, j)[2] = keyVmat.at<uchar>(half_i, half_j);
                }
            }

            
            if(n == 7){
                // cvtColor(keyFrameMat, keyFrameMat, COLOR_YUV2BGR);
                // imwrite("keyFrameMat_inter.jpg", keyFrameMat);
                // for (int i = 0; i < padded_height; i++){
                // for(int i = 0; i < height; i++){
                //     for(int j = 0; j < width; j++){
                //         // cout << (int)keyFrameMat.at<Vec3b>(i, j)[0] << endl;
                //     }
                // }
            }
            int num_blocks_width = padded_width/blockSize;     //number of blocks on the width of the whole frame
            int num_blocks_height = padded_height/blockSize;   //number of blocks on the height of the whole frame
            for (int bw = 0; bw < num_blocks_width; bw++){
                for(int bh = 0; bh < num_blocks_height; bh++){
                    for (int i = 0; i < blockSize; i++){
                        for (int j = 0; j < blockSize; j++){
                            //Getting the current block (predicted)
                            int value = keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorYs[motionY_idx], bw*blockSize + j + motionVectorXs[motionX_idx])[0] + Ydecoded[pixel_idx];
                            if(value > 255) value = 255;
                            if(value < 0) value = 0;
                            YMat.at<uchar>(bh*blockSize + i, bw*blockSize + j) = value;
                            if(n==7){
                                if ((bh*blockSize + i + motionVectorYs[motionY_idx]) > padded_height || (bh*blockSize + i + motionVectorYs[motionY_idx]) < 0){
                                    cerr << "Error: Motion vector[X] out of bounds [0-" << padded_height << "]: " << (bh*blockSize + i + motionVectorYs[motionY_idx]) << endl;
                                    return -1;
                                }
                                if ((bw*blockSize + j + motionVectorXs[motionX_idx]) > padded_width || (bw*blockSize + j + motionVectorXs[motionX_idx]) < 0){
                                    cerr << "Error: Motion vector[Y] out of bounds [0-" << padded_width << "]: " << (bw*blockSize + j + motionVectorXs[motionX_idx]) << endl;
                                    return -1;
                                }
                            }
                            pixel_idx++;
                            if (i % 2 == 0 and j % 2 == 0){
                                int value = keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorYs[motionY_idx], bw*blockSize + j + motionVectorXs[motionX_idx])[1] + Cbdecoded[pixel_idx2];
                                if(value > 255) value = 255;
                                if(value < 0) value = 0;
                                UMat.at<uchar>((bh*blockSize + i)/2, (bw*blockSize + j)/2) = value;
                                value = keyFrameMat.at<Vec3b>(bh*blockSize + i + motionVectorYs[motionY_idx], bw*blockSize + j + motionVectorXs[motionX_idx])[2] + Crdecoded[pixel_idx2];
                                if(value > 255) value = 255;
                                if(value < 0) value = 0;
                                VMat.at<uchar>((bh*blockSize + i)/2, (bw*blockSize + j)/2) = value;
                                pixel_idx2++;
                            }

                        }
                    }
                    motionY_idx++;
                    motionX_idx++;
                }
            }
            vector<int> Y_vector;
            vector<int> Cb_vector;
            vector<int> Cr_vector;

            //convert the matrix back to a vector
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    Y_vector.push_back(YMat.at<uchar>(i, j));
                }
                if(color_space == 420){
                    if (i < height/2 && i < width/2) {
                        for(int j = 0; j < width/2; j++){
                            Cb_vector.push_back(UMat.at<uchar>(i, j));
                            Cr_vector.push_back(VMat.at<uchar>(i, j));
                        }
                    }
                } else if(color_space == 422){
                    if (i < width/2) {
                        for(int j = 0; j < width/2; j++){
                            Cb_vector.push_back(UMat.at<uchar>(i, j));
                            Cr_vector.push_back(VMat.at<uchar>(i, j));
                        }
                    }
                } else if(color_space == 444){
                    for(int j = 0; j < width; j++){
                        Cb_vector.push_back(UMat.at<uchar>(i, j));
                        Cr_vector.push_back(VMat.at<uchar>(i, j));
                    }
                }
            }
            //write the Y_vector to the file
            for(long unsigned int i = 0; i < Y_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Y_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }

            //write the Cb_vector to the file
            for(long unsigned int i = 0; i < Cb_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Cb_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }

            //write the Cr_vector to the file
            for(long unsigned int i = 0; i < Cr_vector.size(); i++){
                //convert the int to a byte
                char byte = (char)Cr_vector[i];
                //write the byte to the file
                out.write(&byte, sizeof(byte));
            }
        }

        if(n < num_frames - 1) out << "FRAME" << endl;
    }

    //end the timer
    end2 = clock();
    elapsed_secs2 = double(end2 - start2) / CLOCKS_PER_SEC * 1000;
    cout << "Time to undo the predictions and write to file: " << elapsed_secs2 << " ms" << endl;

    //close the file
    out.close();

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Execution time: " << elapsed_secs << " ms" << endl;
    return 0;
}
