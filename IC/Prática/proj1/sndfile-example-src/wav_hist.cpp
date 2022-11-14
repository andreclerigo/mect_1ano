#include <iostream>
#include <vector>
#include <sndfile.hh>
#include "wav_hist.h"

using namespace std;

constexpr size_t FRAMES_BUFFER_SIZE = 65536; // Buffer for reading frames

int main(int argc, char *argv[]) {

	if(argc < 3) {
		cerr << "Usage: " << argv[0] << " <input file> <channel | mid | side>\n";
		return 1;
	}

	SndfileHandle sndFile { argv[argc-2] };
	if(sndFile.error()) {
		cerr << "Error: invalid input file\n";
		return 1;
    }

	if((sndFile.format() & SF_FORMAT_TYPEMASK) != SF_FORMAT_WAV) {
		cerr << "Error: file is not in WAV format\n";
		return 1;
	}

	if((sndFile.format() & SF_FORMAT_SUBMASK) != SF_FORMAT_PCM_16) {
		cerr << "Error: file is not in PCM_16 format\n";
		return 1;
	}

	// Choose the channel to process
	// If the channel is not specified, process the mid or side channel
	string mode { argv[argc-1] };
	int channel {};
	if (mode != "mid" && mode != "side") {
		try {
			channel = stoi(argv[argc-1]);
		} catch(exception &err) {
			cerr << "Error: invalid mode requested\n";
			return 1;
		}

		if(channel >= sndFile.channels()) {
			cerr << "Error: invalid channel requested\n";
			return 1;
		}
	}

	size_t nFrames;
	vector<short> samples(FRAMES_BUFFER_SIZE * sndFile.channels());
	WAVHist hist { sndFile };

	while((nFrames = sndFile.readf(samples.data(), FRAMES_BUFFER_SIZE))) {
		samples.resize(nFrames * sndFile.channels());

		// Update the values for channel histogram, mid or side histogram
		hist.update(samples);
		hist.update_mid(samples);
		hist.update_side(samples);
	}

	// Dump the data according to the mode/channel requested
	if (mode == "mid") {
		hist.mid_dump();
	} else if (mode == "side") {
		hist.side_dump();
	} else {
		hist.dump(channel);
	}

	return 0;
}
