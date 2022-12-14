#include "golomb.h"
#include <iostream>
#include <string>
#include <sndfile.hh>
#include "bitStream.h"

using namespace std;

constexpr size_t FRAMES_BUFFER_SIZE = 65536; // Buffer for reading frames

int main( int argc, char** argv ) {

    //function that predicts the next value in the sequence based on 3 previous values
    auto predict = [](int a, int b, int c) {
        //3*a - 3*b + c
        return 3*a - 3*b + c;
    };

    //start a timer
    clock_t start = clock();

    if(argc < 3) {
		cerr << "Usage: " << argv[0] << " <input file> <output file>\n";
		return 1;
	}

    int sampleRate = 44100;
    BitStream bs (argv[1], "r");
    vector<int> v_channels = bs.readBits(16);
    vector<int> v_padding = bs.readBits(16);
    vector<int> v_q = bs.readBits(16);
    vector<int> v_nFrames = bs.readBits(32);
    vector<int> v_blockSize = bs.readBits(16);
    vector<int> v_num_zeros = bs.readBits(16);
    vector<int> v_m_size = bs.readBits(16);

    int nChannels = 0;
    for(long unsigned int i = 0; i < v_channels.size(); i++)
        nChannels += v_channels[i] * pow(2, v_channels.size() - i - 1);

    int padding = 0;
    for(long unsigned int i = 0; i < v_padding.size(); i++)
        padding += v_padding[i] * pow(2, v_padding.size() - i - 1);

    int q = 0;
    for(long unsigned int i = 0; i < v_q.size(); i++)
        q += v_q[i] * pow(2, v_q.size() - i - 1);

    int nFrames = 0;
    for(long unsigned int i = 0; i < v_nFrames.size(); i++)
        nFrames += v_nFrames[i] * pow(2, v_nFrames.size() - i - 1);

    SndfileHandle sfhOut { argv[argc-1], SFM_WRITE, SF_FORMAT_WAV | SF_FORMAT_PCM_16, nChannels, sampleRate };

    int m_size = 0;
    for(long unsigned int i = 0; i < v_m_size.size(); i++) {
        m_size += v_m_size[i] * pow(2, v_m_size.size() - i - 1);
    }

    int blockSize = 0;
    for(long unsigned int i = 0; i < v_blockSize.size(); i++) {
        blockSize += v_blockSize[i] * pow(2, v_blockSize.size() - i - 1);
    }

    int num_zeros = 0;
    for(long unsigned int i = 0; i < v_num_zeros.size(); i++) {
        num_zeros += v_num_zeros[i] * pow(2, v_num_zeros.size() - i - 1);
    }
    
    vector<int> m_vector;
    for(int i = 0; i < m_size; i++) {
        vector<int> v_m_i = bs.readBits(16);
        int m_i = 0;
        for(long unsigned int j = 0; j < v_m_i.size(); j++) {
            m_i += v_m_i[j] * pow(2, v_m_i.size() - j - 1);
        }
        m_vector.push_back(m_i);
    }

    //total size of the file - the size of the header and the size of the m vector
    int total = bs.getFileSize() - (16 + 2*m_size);
    long totalBits = total*8;
    vector<int> v_encoded = bs.readBits(totalBits);
    //convert vector<int> of bits to string of bits
    string encoded = "";
    for(long unsigned int  i = 0; i < v_encoded.size(); i++) {
        encoded += to_string(v_encoded[i]);
    }

    //discard the last num_zeros bits
    encoded = encoded.substr(0, encoded.size() - num_zeros);

    //decode looping through the v_m vector
    Golomb g;
    vector<int> decoded;
    if (m_size == 1)
        decoded = g.decode(encoded, m_vector[0]);
    else 
        decoded = g.decodeMultiple(encoded, m_vector, blockSize);

    vector<short> samplesVector;

    if (nChannels < 2) {
        for (long unsigned int i = 0; i < decoded.size(); i++) {
            if (i >= 3) {
                int difference = decoded[i] + predict(samplesVector[i-1], samplesVector[i-2], samplesVector[i-3]);
                samplesVector.push_back(difference);
            }
            else samplesVector.push_back(decoded[i]);
        }
    } else {
        //da para otimizar apenas com um ciclo e condicionando o i
        for (int i = 0; i < nFrames; i++) {
            if (i >= 3) {
                int difference = decoded[i] + predict(samplesVector[i-1], samplesVector[i-2], samplesVector[i-3]);
                samplesVector.push_back(difference);
            }
            else samplesVector.push_back(decoded[i]); 
        }

        for (long unsigned int i = nFrames; i < decoded.size(); i++) {
            if ((int) i >= nFrames + 3) {
                int difference = decoded[i] + predict(samplesVector[i-1], samplesVector[i-2], samplesVector[i-3]);
                samplesVector.push_back(difference);
            }
            else samplesVector.push_back(decoded[i]); 
        }

        //merge the two channels into one vector
        vector<short> merged;
        //the first channel is the first nFrames samples
        vector<short> firstChannel(samplesVector.begin(), samplesVector.begin() + nFrames);
        //the second channel is the last nFrames samples
        vector<short> secondChannel(samplesVector.begin() + nFrames, samplesVector.end());
        for(int i = 0; i < nFrames; i++) {
            merged.push_back(firstChannel[i]);
            merged.push_back(secondChannel[i]);
        }
        samplesVector = merged;
    }

    //quantize the samples
    if (q != 0) {
        if(q != 1){
            for (long unsigned int i = 0; i < samplesVector.size(); i++) {
                //shift the sample to the left by 1 bit
                samplesVector[i] = samplesVector[i] << 1;
                //change that last bit to 1
                samplesVector[i] = samplesVector[i] | 1;
                // //shif the sample to the left by q-1 bits
                samplesVector[i] = samplesVector[i] << (q-1);
            }
        } else {
            for (long unsigned int i = 0; i < samplesVector.size(); i++) {
                //shift the sample to the left by 1 bit
                samplesVector[i] = samplesVector[i] << 1;
            }
        }
    }

    //remove the last int padding values
    samplesVector = vector<short>(samplesVector.begin(), samplesVector.end() - padding);
    //write to wav file the samples vector
    sfhOut.write(samplesVector.data(), samplesVector.size());
    bs.close();

    //end timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //in milliseconds
    cout << "Time elapsed: " << elapsed_secs*1000 << " ms" << endl;

    return 0;
}