#ifndef WAVHIST_H
#define WAVHIST_H

#include <iostream>
#include <vector>
#include <map>
#include <sndfile.hh>

class WAVHist {
  private:
	std::vector<std::map<short, size_t>> counts;
	std::vector<std::map<short, size_t>> mid_counts;
	std::vector<std::map<int, size_t>> side_counts;

  public:
	WAVHist(const SndfileHandle& sfh) {
		counts.resize(sfh.channels());
		mid_counts.resize(1);
		side_counts.resize(1);
	}

	void update(const std::vector<short>& samples) {
		size_t n { };
		for(auto s : samples)
			counts[n++ % counts.size()][s]++;
	}

	void dump(const size_t channel) const {
		for(auto [value, counter] : counts[channel])
			std::cout << value << '\t' << counter << '\n';
	}

	void mid_dump() const {
		for(auto [value, counter] : mid_counts[0])
			std::cout << value << '\t' << counter << '\n';
	}

	void side_dump() const {
		for(auto [value, counter] : side_counts[0])
			std::cout << value << '\t' << counter << '\n';
	}

	void update_mid(const std::vector<short>& samples) {
		for(long unsigned int i = 0; i < samples.size()/2; i++)
			mid_counts[0][(samples[2*i] + samples[2*i+1]) / 2]++;
	}

	void update_side(const std::vector<short>& samples) {
		for(long unsigned int i = 0; i < samples.size()/2; i++)
			side_counts[0][(samples[2*i] - samples[2*i+1]) / 2]++;
	}
};

#endif

