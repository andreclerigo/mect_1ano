#include "golomb.h"
#include <iostream>
#include <string>
#include <string.h>
#include <vector>
#include "opencv2/opencv.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "bitStream.h"

using namespace std;
using namespace cv;

int main(int argc, char *argv[]) {

    auto ycbcr2rgb = [](Mat img) {
        // convert to YCbCr
        Mat rgb;
        cvtColor(img, rgb, COLOR_YCrCb2BGR);
        return rgb;
    };

    auto predict = [](int a, int b, int c, int mode){
        // with a switch case, we can choose the prediction mode
        //mode = 0 -> average
        //mode = 1 -> left
        //mode = 2 -> above
        //mode = 3 -> left top
        //mode = 4 -> left + above - left top
        //mode = 5 -> left + (above - left top) / 2
        //mode = 6 -> above + (left - left top) / 2
        //mode = 7 -> left + above / 2
        //mode = 8 -> non linear prediction:
        //min(left, above) if  left top >= max(left, above)
        //max(left, above) if  left top <= min(left, above)
        //otherwise, left + above - left top

        switch(mode) {
            case 0:
                return (a + b + c) / 3;
            case 1:
                return a;
            case 2:
                return b;
            case 3:
                return c;
            case 4:
                return a + b - c;
            case 5:
                return a + (b - c) / 2;
            case 6:
                return b + (a - c) / 2;
            case 7:
                return (a + b) / 2;
            case 8:
                if (c >= max(a, b))
                    return min(a, b);
                else if (c <= min(a, b))
                    return max(a, b);
                else
                    return a + b - c;
        }
        return 0;
    };

    //start a timer
    clock_t start = clock();

    if (argc < 3) {
		cerr << "Usage: " << argv[0] << " <input file> <output file>\n";
		return 1;
	}

    BitStream bs (argv[1], "r");
    string output = argv[2];
    vector<int> v_imgtype = bs.readBits(32);
    vector<int> v_mode = bs.readBits(16);
    vector<int> v_imgwidth = bs.readBits(16);
    vector<int> v_imgheight = bs.readBits(16);
    vector<int> v_bs = bs.readBits(16);
    vector<int> v_encY = bs.readBits(32);
    vector<int> v_encCb = bs.readBits(32);
    vector<int> v_encCr = bs.readBits(32);
    vector<int> v_msize = bs.readBits(16);

    int imgtype = 0;
    for(long unsigned int i = 0; i < v_imgtype.size(); i++)
        imgtype += v_imgtype[i] * pow(2, v_imgtype.size() - i - 1);

    int mode = 0;
    for(long unsigned int i = 0; i < v_mode.size(); i++)
        mode += v_mode[i] * pow(2, v_mode.size() - i - 1);

    int imgwidth = 0;
    for(long unsigned int i = 0; i < v_imgwidth.size(); i++)
        imgwidth += v_imgwidth[i] * pow(2, v_imgwidth.size() - i - 1);

    int imgheight = 0;
    for(long unsigned int i = 0; i < v_imgheight.size(); i++)
        imgheight += v_imgheight[i] * pow(2, v_imgheight.size() - i - 1);

    int blockSize = 0;
    for(long unsigned int i = 0; i < v_bs.size(); i++)
        blockSize += v_bs[i] * pow(2, v_bs.size() - i - 1);

    int encY = 0;
    for(long unsigned int i = 0; i < v_encY.size(); i++)
        encY += v_encY[i] * pow(2, v_encY.size() - i - 1);

    int encCb = 0;
    for(long unsigned int i = 0; i < v_encCb.size(); i++)
        encCb += v_encCb[i] * pow(2, v_encCb.size() - i - 1);

    int encCr = 0;
    for(long unsigned int i = 0; i < v_encCr.size(); i++)
        encCr += v_encCr[i] * pow(2, v_encCr.size() - i - 1);

    int msize = 0;
    for(long unsigned int i = 0; i < v_msize.size(); i++)
        msize += v_msize[i] * pow(2, v_msize.size() - i - 1);

    Mat new_image = Mat::zeros(imgheight, imgwidth, imgtype);

    vector<int> Ym_vector;
    for (int i = 0; i < msize; i++) {
        vector<int> v_Ym = bs.readBits(16);
        int Ym = 0;
        for(long unsigned int i = 0; i < v_Ym.size(); i++) {
            Ym += v_Ym[i] * pow(2, v_Ym.size() - i - 1);
        }
        Ym_vector.push_back(Ym);
    }

    vector<int> Cbm_vector;
    for(int i = 0; i < msize; i++) {
        vector<int> v_Cbm = bs.readBits(16);
        int Cbm = 0;
        for(long unsigned int i = 0; i < v_Cbm.size(); i++) {
            Cbm += v_Cbm[i] * pow(2, v_Cbm.size() - i - 1);
        }
        Cbm_vector.push_back(Cbm);
    }

    vector<int> Crm_vector;
    for(int i = 0; i < msize; i++) {
        vector<int> v_Crm = bs.readBits(16);
        int Crm = 0;
        for(long unsigned int i = 0; i < v_Crm.size(); i++) {
            Crm += v_Crm[i] * pow(2, v_Crm.size() - i - 1);
        }
        Crm_vector.push_back(Crm);
    }

    vector<int> Y_values = bs.readBits(encY);
    vector<int> Cb_values = bs.readBits(encCb);
    vector<int> Cr_values = bs.readBits(encCr);

    string YencodedString = "";
    for(long unsigned int i = 0; i < Y_values.size(); i++) {
        YencodedString += to_string(Y_values[i]);
    }
    string CbencodedString = "";
    for(long unsigned int i = 0; i < Cb_values.size(); i++) {
        CbencodedString += to_string(Cb_values[i]);
    }
    string CrencodedString = "";
    for(long unsigned int i = 0; i < Cr_values.size(); i++) {
        CrencodedString += to_string(Cr_values[i]);
    }

    Golomb g;
    vector<int> Ydecoded;
    vector<int> Cbdecoded;
    vector<int> Crdecoded;

    if (msize == 1) {
        Ydecoded = g.decode(YencodedString, Ym_vector[0]);
        Cbdecoded = g.decode(CbencodedString, Cbm_vector[0]);
        Crdecoded = g.decode(CrencodedString, Crm_vector[0]);
    } else {
        Ydecoded = g.decodeMultiple(YencodedString, Ym_vector, imgwidth);
        Cbdecoded = g.decodeMultiple(CbencodedString, Cbm_vector, imgwidth);
        Crdecoded = g.decodeMultiple(CrencodedString, Crm_vector, imgwidth);
    }

    //undo the predictions
    int pixel_idx = 0;
    for (int i = 0; i < imgheight; i++) {
        for (int j = 0; j < imgwidth; j++) {
            if (i == 0 && j == 0) {
                //create a new pixel with the decoded values of Y, Cb and Cr at the current pixel index and add it to the image without RGB conversion
                new_image.at<Vec3b>(j, i) = Vec3b(Ydecoded[pixel_idx], Cbdecoded[pixel_idx], Crdecoded[pixel_idx]);
                pixel_idx++;
            } else if (i == 0) {
                //if its the first line of the image, use only the previous pixel (to the left)
                int Y = new_image.at<Vec3b>(i, j-1)[0] + Ydecoded[pixel_idx];
                int Cb = new_image.at<Vec3b>(i, j-1)[1] + Cbdecoded[pixel_idx];
                int Cr = new_image.at<Vec3b>(i, j-1)[2] + Crdecoded[pixel_idx];
                new_image.at<Vec3b>(i, j) = Vec3b(Y, Cb, Cr);
                pixel_idx++;
            } else if (j == 0) {
                //if its the first pixel of the line, use only the pixel above
                int Y = new_image.at<Vec3b>(i-1, j)[0] + Ydecoded[pixel_idx];
                int Cb = new_image.at<Vec3b>(i-1, j)[1] + Cbdecoded[pixel_idx];
                int Cr = new_image.at<Vec3b>(i-1, j)[2] + Crdecoded[pixel_idx];
                new_image.at<Vec3b>(i, j) = Vec3b(Y, Cb, Cr);
                pixel_idx++;
            } else {
                //if its not the first pixel of the image nor the first line, use the 3 pixels to the left, above and to the left top
                int Y = int(predict(new_image.at<Vec3b>(i, j-1)[0], new_image.at<Vec3b>(i-1, j)[0], new_image.at<Vec3b>(i-1, j-1)[0], mode)) + Ydecoded[pixel_idx];
                int Cb = int(predict(new_image.at<Vec3b>(i, j-1)[1], new_image.at<Vec3b>(i-1, j)[1], new_image.at<Vec3b>(i-1, j-1)[1], mode)) + Cbdecoded[pixel_idx];
                int Cr = int(predict(new_image.at<Vec3b>(i, j-1)[2], new_image.at<Vec3b>(i-1, j)[2], new_image.at<Vec3b>(i-1, j-1)[2], mode)) + Crdecoded[pixel_idx];
                new_image.at<Vec3b>(i, j) = Vec3b(Y, Cb, Cr);
                pixel_idx++;
            }
        }
    }

    //convert the image from YCbCr to RGB
    new_image = ycbcr2rgb(new_image);

    //save the image
    imwrite(output, new_image);

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    elapsed_secs = elapsed_secs * 1000;
    cout << "Execution time: " << elapsed_secs << " ms" << endl;

    return 0;
}
