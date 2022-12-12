#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>

using namespace std;
using namespace cv;

int main(int argc, char** argv) {
    //start a timer
    clock_t start = clock();
    
    if (argc < 4) {	
        cerr << "Usage: " << argv [0] << " <input file> <output file> <h | v> [view]" << endl;
        return 1 ;
    }
    
    Mat img = imread(argv[1]);

    if (img.empty()) {
        cerr << "Could not open or find the image!" << endl; ;
        cerr << "Usage: " << argv [0] << " <input file> <output file> <h | v> [view]" << endl;
        return -1;
    }

    Mat mirror = Mat::zeros(img.size(), img.type());
    int direction = 0;

    if (string(argv[3]) == "h") {
        direction = 1;
    } else if (string(argv[3]) == "v") {
        direction = 0;
    } else {
        cerr << "Usage: " << argv [0] << " <input file> <output file> <h | v> [view]" << endl;
        return 1 ;
    }

    int nRows = img.rows;
    int nCols = img.cols;       // All channels at the same time

    for (int i = 0; i < nRows; i++) {
        for (int j = 0; j < nCols; j++) {
            if (direction) {
                mirror.at<Vec3b>(nRows - 1 - i, j) = img.at<Vec3b>(i, j);
            } else {
                mirror.at<Vec3b>(i, nCols - 1 - j) = img.at<Vec3b>(i, j);
            }
        }
    }

    imwrite(argv[2], mirror);

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Time: " << elapsed_secs << " ms" << endl;

    try {
        if (string(argv[4]) == "view") {
            imshow("Flipped Image", mirror);
            waitKey(0);
        }
    } catch (exception& e) {
        return 0;
    }

    return 0;
}
