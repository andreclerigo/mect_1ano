// lossy version of the video codec based on Golomb coding of the prediction residuals
// i.e. by simply quantization of those residuals

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <iostream>
#include <string>
#include <vector>
#include "YUV4MPEG2Reader.h"
#include "golomb.h"
#include "bitStream.h"
#include "opencv2/opencv.hpp"

using namespace std;
using namespace cv;

// this is a program to help assessing the quality of the lossy compressed videos sequence
// it compares 2 video sequences in terms of the peak signal to noise ratio (PSNR)
// with the expression PSNR = 10 * log10(A^2 / e^2)
// where A is the maximum value of the pixel (255 for 8-bit images)
// and e^2 is the mean squared error between the reconstructed, f~, and the original frames, f

// e^2 = 1/(M*N) * sum_r_to_N(sum_c_M((f-f~)^2))
// where N and M are the number of rows and columns of the video frames

int main(int argc, char *argv[]) {

    if (argc != 3) {
        cout << "Usage: " << argv[0] << " <original input file> <reconstructed input file>" << endl;
        return 1;
    }

    YUV4MPEG2Reader video1(argv[1]);
    YUV4MPEG2Reader video2(argv[2]);

    int width1 = video1.width();
    int height1 = video1.height();

    int width2 = video2.width();
    int height2 = video2.height();

    FILE* input1 = fopen(argv[1], "r");
    FILE* input2 = fopen(argv[2], "r");

    // check if the two videos have the same size
    if (video1.width() != video2.width() || video1.height() != video2.height()) {
        cout << "The two videos must have the same dimensions!" << endl;
        cout << "Video 1 has: " << video1.width() << "x" << video1.height() << endl;
        cout << "Video 2 has: " << video2.width() << "x" << video2.height() << endl;
        return 1;
    }


    // get the number of frames
    int num_frames1 = video1.numFrames();
    int num_frames2 = video2.numFrames();

    // check if the two videos have the same number of frames
    if (num_frames1 != num_frames2) {
        cout << "The two videos have different number of frames" << endl;
        return 1;
    }

    // vector for PSNR values
    vector<double> Ypsnr_values;
    vector<double> Upsnr_values;
    vector<double> Vpsnr_values;

    //YUV vectors
    vector<int> Y1 = vector<int>(width1 * height1);
    vector<int> U1 = vector<int>(width1 * height1 / 4);
    vector<int> V1 = vector<int>(width1 * height1 / 4);
    vector<int> Y2 = vector<int>(width2 * height2);
    vector<int> U2 = vector<int>(width2 * height2 / 4);
    vector<int> V2 = vector<int>(width2 * height2 / 4);

    char line[100]; //Read the line after the header

    bool finish = false;

    // loop through the frames of the two videos
    while(!feof(input1)){
        //VIDEO 1
        //read the frame line
        fgets(line, 100, input1);
        //read the Y data  (Height x Width)
        for(int i = 0; i < width1 * height1; i++){
            int value = fgetc(input1);
            Y1[i] = value;
            if(value < 0){
                finish = true;
                break;
            }
        }
        if(finish) break;
        for(int i = 0; i < width1 * height1 / 4; i++) U1[i] = fgetc(input1); //read the U data (Height/2 x Width/2)
        for(int i = 0; i < width1 * height1 / 4; i++) V1[i] = fgetc(input1); //read the V data (Height/2 x Width/2)

        //VIDEO 2
        //read the frame line
        fgets(line, 100, input2);
        //read the Y data  (Height x Width)
        for(int i = 0; i < width2 * height2; i++){
            int value = fgetc(input2);
            Y2[i] = value;
            if(value < 0){
                finish = true;
                break;
            }
        }
        if(finish) break;
        for(int i = 0; i < width2 * height2 / 4; i++) U2[i] = fgetc(input2); //read the U data (Height/2 x Width/2)
        for(int i = 0; i < width2 * height2 / 4; i++) V2[i] = fgetc(input2); //read the V data (Height/2 x Width/2)

        Mat YMat1 = Mat(height1, width1, CV_8UC1);
        Mat UMat1 = Mat(height1/2, width1/2, CV_8UC1);
        Mat VMat1 = Mat(height1/2, width1/2, CV_8UC1);

        Mat YMat2 = Mat(height2, width2, CV_8UC1);
        Mat UMat2 = Mat(height2/2, width2/2, CV_8UC1);
        Mat VMat2 = Mat(height2/2, width2/2, CV_8UC1);

        for(int i = 0; i < height1; i++){
            for(int j = 0; j < width1; j++) YMat1.at<uchar>(i, j) = Y1[i * width1 + j];
            if (i < height1/2 && i < width1/2) {
                for(int j = 0; j < width1/2; j++){
                    UMat1.at<uchar>(i, j) = U1[i * width1/2 + j];
                    VMat1.at<uchar>(i, j) = V1[i * width1/2 + j];
                }
            }
        }

        for(int i = 0; i < height2; i++){
            for(int j = 0; j < width2; j++) YMat2.at<uchar>(i, j) = Y2[i * width2 + j];
            if (i < height2/2 && i < width2/2) {
                for(int j = 0; j < width2/2; j++){
                    UMat2.at<uchar>(i, j) = U2[i * width2/2 + j];
                    VMat2.at<uchar>(i, j) = V2[i * width2/2 + j];
                }
            }
        }

        //psnr calculation
        //Y
        double psnr;
        long int sum = 0;
        for(int i = 0; i < height1; i++){
            for(int j = 0; j < width1; j++){
                // cout << int(YMat1.at<uchar>(i, j)) << " " << int(YMat2.at<uchar>(i, j)) <<  " = " << pow(YMat1.at<uchar>(i, j) - YMat2.at<uchar>(i, j), 2) << endl;
                sum += pow(YMat1.at<uchar>(i, j) - YMat2.at<uchar>(i, j), 2);
                
                
            }
        }
        double e2 = (double)sum / (width1 * height1);
        psnr = 10 * log10(255 * 255 / e2);
        Ypsnr_values.push_back(psnr);

        //U
        sum = 0;
        for(int i = 0; i < height1/2; i++){
            for(int j = 0; j < width1/2; j++){
                sum += pow(UMat1.at<uchar>(i, j) - UMat2.at<uchar>(i, j), 2);
            }
        }
        e2 = sum / (width1/2 * height1/2);
        psnr = 10 * log10(255 * 255 / e2);
        Upsnr_values.push_back(psnr);

        //V
        sum = 0;
        for(int i = 0; i < height1/2; i++){
            for(int j = 0; j < width1/2; j++){
                sum += pow(VMat1.at<uchar>(i, j) - VMat2.at<uchar>(i, j), 2);
            }
        }
        e2 = sum / (width1/2 * height1/2);
        psnr = 10 * log10(255 * 255 / e2);
        Vpsnr_values.push_back(psnr);
    }


    //calculate the lowest psnr for each component
    double Ypsnr = 0;
    double Upsnr = 0;
    double Vpsnr = 0;

    for(long unsigned int i = 0; i < Ypsnr_values.size(); i++){
        //if Ypnsr is infinite, Ypsnr is 100
        if(Ypsnr_values[i] == numeric_limits<double>::infinity()) Ypsnr_values[i] = 100;
        Ypsnr += Ypsnr_values[i];
        if(Upsnr_values[i] == numeric_limits<double>::infinity()) Upsnr_values[i] = 100;
        Upsnr += Upsnr_values[i];
        if(Vpsnr_values[i] == numeric_limits<double>::infinity()) Vpsnr_values[i] = 100;
        Vpsnr += Vpsnr_values[i];
    }

    Ypsnr /= Ypsnr_values.size();
    Upsnr /= Upsnr_values.size();
    Vpsnr /= Vpsnr_values.size();

    //print the results
    if (Ypsnr == 100 ) cout << "YPSNR: " << "inf" << endl;
    else cout << "YPSNR: " << Ypsnr << endl;

    if (Upsnr == 100 ) cout << "UPSNR: " << "inf" << endl;
    else cout << "UPSNR: " << Upsnr << endl;

    if (Vpsnr == 100 ) cout << "VPSNR: " << "inf" << endl;
    else cout << "VPSNR: " << Vpsnr << endl;

    //average over all components
    double psnr = (Ypsnr + Upsnr + Vpsnr) / 3;
    if (psnr == 100 ) cout << "PSNR: " << "inf" << endl;
    else cout << "PSNR: " << psnr << endl;

    return 0;
    
}


