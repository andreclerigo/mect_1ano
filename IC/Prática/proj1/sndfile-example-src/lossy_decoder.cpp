#include <iostream>
#include <vector>
#include <cmath>
#include <fftw3.h>
#include <sndfile.hh>
#include "bitStream.h"

using namespace std;

int main (int argc, char *argv[])
{

    //start a timer 
    clock_t start = clock();

    if(argc < 3 || argc > 3 ){
		cerr << "Usage: " << argv[0] << " <input file (binary)> <output file>\n";
		return 1;
	}

    string inputFileName = argv[1];

    BitStream bitStream(inputFileName, "r");

    vector<int> v_bs = bitStream.readBits(16);
    vector<int> v_nBlocks= bitStream.readBits(16);
    vector<int> v_nChannels = bitStream.readBits(16);
    vector<int> v_sampleRate = bitStream.readBits(16);
    vector<int> v_nFrames = bitStream.readBits(32);

    //convert vector<int> to int
    int bs = 0;
    for(int i = 0; i < v_bs.size(); i++) {
        bs += v_bs[i] * pow(2, v_bs.size() - i - 1);
    }

    int nBlocks = 0;
    for(int i = 0; i < v_nBlocks.size(); i++) {
        nBlocks += v_nBlocks[i] * pow(2, v_nBlocks.size() - i - 1);
    }

    int nChannels = 0;
    for(int i = 0; i < v_nChannels.size(); i++) {
        nChannels += v_nChannels[i] * pow(2, v_nChannels.size() - i - 1);
    }


    int sampleRate = 0;
    for(int i = 0; i < v_sampleRate.size(); i++) {
        sampleRate += v_sampleRate[i] * pow(2, v_sampleRate.size() - i - 1);
    }

    int nFrames = 0;
    for(int i = 0; i < v_nFrames.size(); i++) {
        nFrames += v_nFrames[i] * pow(2, v_nFrames.size() - i - 1);
    }

    // cout << "bs: " << bs << endl;
    // cout << "nBlocks: " << nBlocks << endl;
    // cout << "nChannels: " << nChannels << endl;
    // cout << "sampleRate: " << sampleRate << endl;
    // cout << "nFrames: " << nFrames << endl;

    SndfileHandle sfhOut { argv[argc-1], SFM_WRITE, SF_FORMAT_WAV | SF_FORMAT_PCM_16, nChannels, sampleRate };
	if(sfhOut.error()) {
		cerr << "Error: invalid output file\n";
		return 1;
    }

    int total = bitStream.getFileSize() - 12;
    long totalBits = total*8;

    vector<int> x_dct_bits = bitStream.readBits(totalBits);
    vector<vector<double>> x_dct(nChannels, vector<double>(nBlocks * bs));
    vector<int> tmp;

    
    for(int i = 0; i < x_dct_bits.size(); i+=32) {
        //each 32 bits is a int (signed)
        int temp = 0;
        
        vector<int> reversed_temp;

        for(int j = 31; j >= 0; j--) {
            reversed_temp.push_back(x_dct_bits[i+j]);
        }

        //convert to int
        for(int j = 0; j < reversed_temp.size(); j++) {
            temp += reversed_temp[j] * pow(2, reversed_temp.size() - j - 1);
        }
        tmp.push_back(temp);
    }

    bitStream.close();

    int count = 0;
    for(int n = 0; n < nBlocks; n++) {
        for(int c = 0; c < nChannels; c++) {
            for (int k = 0; k < bs; k++) {
                //divide temp by 100 to get the original value as a decimal with 2 decimal places
                x_dct[c][n*bs + k] = tmp[count]/100.0;
                count++;
            }
        }
    }

    vector<double> x(bs);
    vector<short> samples(nChannels * nFrames);
    samples.resize(nBlocks * bs * nChannels);
    
    // Inverse DCT
	fftw_plan plan_i = fftw_plan_r2r_1d(bs, x.data(), x.data(), FFTW_REDFT01, FFTW_ESTIMATE);
	for(size_t n = 0 ; n < nBlocks ; n++)
		for(size_t c = 0 ; c < nChannels ; c++) {
			for(size_t k = 0 ; k < bs ; k++){
				x[k] = x_dct[c][n * bs + k];
                // cout << x[k] << endl;
            }

			fftw_execute(plan_i);
			for(size_t k = 0 ; k < bs ; k++)
				samples[(n * bs + k) * nChannels + c] = static_cast<short>(round(x[k]));

		}
    
    
    sfhOut.writef(samples.data(), nFrames);

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Time: " << elapsed_secs << " ms" << endl;
    return 0;

}
