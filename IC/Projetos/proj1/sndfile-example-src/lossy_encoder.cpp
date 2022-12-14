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

    size_t bs { 1024 };
    size_t discarded_units_per_block { 8 };

    if(argc < 5) {
		cerr << "Usage: " << argv[0] << " <input file> <output file> <block_size> <discarded_units_per_block> \n";
		return 1;
	}

    SndfileHandle sfhIn { argv[argc-4] };
    if(sfhIn.error()) {
        cerr << "Error: invalid input file\n";
        return 1;
    }

    if((sfhIn.format() & SF_FORMAT_TYPEMASK) != SF_FORMAT_WAV) {
        cerr << "Error: file is not in WAV format\n";
        return 1;
    }

    if((sfhIn.format() & SF_FORMAT_SUBMASK) != SF_FORMAT_PCM_16) {
        cerr << "Error: file is not in PCM_16 format\n";
        return 1;
    }

    //assert that discarded_units_per_block is < block_size
    if(stoi(argv[argc-1]) > stoi(argv[argc-2])) {
        cerr << "Error: discarded_units_per_block must be < block_size\n";
        return 1;
    }

    string outputFileName = argv[2];

    bs = atoi(argv[3]);
    discarded_units_per_block = atoi(argv[4]);

    //assert discarded_units_per_block is < bs
    if(discarded_units_per_block > bs) {
        cerr << "Error: <discarded_units_per_block> must be less than block_size\n";
        return 1;
    }

    size_t nChannels { static_cast<size_t>(sfhIn.channels()) };
	size_t nFrames { static_cast<size_t>(sfhIn.frames()) };

    vector<short> samples(nChannels * nFrames);
	sfhIn.readf(samples.data(), nFrames);

	size_t nBlocks { static_cast<size_t>(ceil(static_cast<double>(nFrames) / bs)) };

	// Do zero padding, if necessary
	samples.resize(nBlocks * bs * nChannels);
    
    // Vector for holding all DCT coefficients, channel by channel
	vector<vector<double>> x_dct(nChannels, vector<double>(nBlocks * bs));

	// Vector for holding DCT computations
	vector<double> x(bs);

    int tmp = 0;

    // Direct DCT
	fftw_plan plan_d = fftw_plan_r2r_1d(bs, x.data(), x.data(), FFTW_REDFT10, FFTW_ESTIMATE);
	for(size_t n = 0 ; n < nBlocks ; n++)
		for(size_t c = 0 ; c < nChannels ; c++) {
			for(size_t k = 0 ; k < bs ; k++)
				x[k] = samples[(n * bs + k) * nChannels + c];

			fftw_execute(plan_d);
			
			for(size_t k = 0 ; k < bs - discarded_units_per_block ; k++){
				x_dct[c][n * bs + k] = x[k] / (bs << 1) * 100;
            }
            tmp++;

		}
    BitStream outputFile (outputFileName, "w") ;
    vector<int> bits; 

    
    //the first 16 bits of the output file will be the the block size converted to binary
    for (int i = 15; i >= 0; i--){
        bits.push_back((bs >> i) & 1);
    }
    
    //the next 16 bits of the output file will be the nBlocks converted to binary
    for (int i = 15; i >= 0; i--){
        bits.push_back((nBlocks >> i) & 1);
    }

    //the next 16 bits of the output file will be the nChannels converted to binary
    for (int i = 15; i >= 0; i--){
        bits.push_back((nChannels >> i) & 1);
    }

    //the  next 16 bits are the sfhIn.samplerate() converted to binary
    for (int i = 15; i >= 0; i--){
        bits.push_back((sfhIn.samplerate() >> i) & 1);
    }

    //the next 32 bits are the nFrames converted to binary
    for (int i = 31; i >= 0; i--){
        bits.push_back((nFrames >> i) & 1);
    }

    int value = 0;
    vector<int>values;
    //all the next bits are x_dct converted to binary
    for(size_t n = 0 ; n < nBlocks ; n++)
		for(size_t c = 0 ; c < nChannels ; c++) {
			for(size_t k = 0 ; k < bs ; k++){
                int tmp2 = x_dct[c][n * bs + k];
				values.push_back(tmp2);
                // cout << tmp2 << endl;
            }
        }

    int count = 0;

    // cout << "bs: " << bs << endl;
    // cout << "nBlocks: " << nBlocks << endl;
    // cout << "nChannels: " << nChannels << endl;
    // cout << "sfhIn.samplerate(): " << sfhIn.samplerate() << endl;
    // cout << "nFrames: " << nFrames << endl;

    for(size_t n = 0 ; n < values.size() ; n++){
        // cout << values[n] << endl;
        // cout << "n: " << n << endl;
        vector<int> temp_array (32);
        //convert values[n] to binary
        for (int i = 31; i >= 0; i--){
            temp_array[i] = (values[n] >> i) & 1;
        }
        //reverse the temp_array
        vector<int> temp_array2 (32);
        for (int i = 0; i < 32; i++){
            temp_array2[i] = temp_array[31-i];
        }

        //push back all the bits of temp_array2 to bits
        for (int i = 31; i >=0; i--){
            bits.push_back(temp_array2[i]);
        }
    }

    
    outputFile.writeBits(bits);
    outputFile.close();

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Time: " << elapsed_secs << " ms" << endl;
    
    return 0;
    
}