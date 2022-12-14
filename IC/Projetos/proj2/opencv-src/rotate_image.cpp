#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>
 
using namespace cv;
using namespace std;

int main(int argc, char *argv[]) {
    // start a timer
    clock_t start = clock();
    
    if (argc < 4) {
        cerr << "Usage: " << argv[0] << " <image file> <output file> <degrees_to_rotate> [view]" << endl;
        return 1;
    }

    // reading image file
    Mat img = imread(argv[1]);
    if (img.empty()) {
        cerr << "Could not open or find the image!" << endl; ;
        cerr << "Usage: " << argv[0] << " <image file> <output file> <degrees_to_rotate> [view]" << endl;
        return -1;
    }

    // verify that degrees_to_rotate is a multiple of 90
    int degrees_to_rotate = atoi(argv[3]);
    if (atoi(argv[3]) % 90 != 0) {
        cerr << "Degrees to rotate must be a multiple of 90" << endl;
        return 1;
    }

    // rotate the image
    int rotatedRows = degrees_to_rotate % 180 ? img.size().width : img.size().height;
    int rotatedCols = degrees_to_rotate % 180 ? img.size().height : img.size().width;
    Mat rotated_image = Mat::zeros(rotatedRows, rotatedCols, img.type());

    int nRows = img.rows;
    int nCols = img.cols;       // All channels at the same time

    for (int i = 0; i < nRows; i++) {
        for (int j = 0; j < nCols; j++) {
            // Stays the same
            if (degrees_to_rotate % 360 == 0) {
                rotated_image.at<Vec3b>(i, j) = img.at<Vec3b>(i, j);
            } else if (degrees_to_rotate % 360 == 270) {
                rotated_image.at<Vec3b>(j, nRows - 1 - i) = img.at<Vec3b>(i, j);
            } else if (degrees_to_rotate % 360 == 180) {
                rotated_image.at<Vec3b>(nRows - 1 - i, nCols - 1 - j) = img.at<Vec3b>(i, j);
            } else if (degrees_to_rotate % 360 == 90) {
                rotated_image.at<Vec3b>(nCols - 1 - j, i) = img.at<Vec3b>(i, j);
            }
        }
    }

    imwrite(argv[2], rotated_image);

    // end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    // print the time in miliseconds
    cout << "Time: " << elapsed_secs*1000 << " ms" << endl;

    try {
        if (string(argv[4]) == "view") {
            imshow("Rotated Image", rotated_image);
            waitKey(0);
        }
    } catch (exception& e) {
        return 0;
    }

    return 0;
}
