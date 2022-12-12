#include "opencv2/opencv.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>

using namespace cv;
using namespace std;

int main( int argc, char** argv ) {

    //start a timer 
    clock_t start = clock();

    if (argc < 3){
        cout << "Usage: "<< argv[0] <<" <input file> <output file> [view]" << endl;
        return -1;
    }

    // read the image
    Mat img = imread(argv[1]);
    // check if the image is loaded
    if (img.empty()){
        cout << "Could not load image: " << argv[1] << endl;
        return -1;
    }

    // loop through the image and copy the pixels to a new image
    Mat new_image = Mat::zeros(img.size(), img.type());
    int channels = img.channels();
    int nRows = img.rows;
    int nCols = img.cols * channels;

    // check if the image is continuous
    if (img.isContinuous()){
        nCols *= nRows;
        nRows = 1;
    }

    // loop through the image and copy the pixels to a new image
    uchar* pixel;
    for (int i = 0; i < nRows; i++){
        pixel = img.ptr<uchar>(i);
        // loop through the columns
        for (int j = 0; j < nCols; j++){
            // copy the pixel to the new image
            new_image.at<uchar>(i,j) = pixel[j];
        }
    }

    // write the new image
    imwrite(argv[2], new_image);   

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Time taken: " << elapsed_secs << " ms" << endl;

    try {
        if (string(argv[3]) == "view") {
            imshow("Coppied Image", img);
            waitKey(0);
        }
    } catch (exception& e) {
        return 0;
    }

    return 0;
}
