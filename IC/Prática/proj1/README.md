# IC - Project 1

## Compile

Go to `proj1/sndfile-example-src` and run `make all`

## Testing

Go to `proj1/sndfile-example-bin`

<br>

### Exercise 2

**For left channel**
```bash
./wav_hist ../sndfile-example-src/sample.wav 0 > left
```  
To get the histogram run `gnuplot` and `plot "left" with boxes`

<br>

**For right channel**
```bash
./wav_hist ../sndfile-example-src/sample.wav 1 > right
```  
To get the histogram run `gnuplot` and `plot "right" with boxes`

<br>

**For mid channel**
```bash
./wav_hist ../sndfile-example-src/sample.wav mid > mid
```  
To get the histogram run `gnuplot` and `plot "mid" with boxes`

<br>

**For side channel**
```bash
./wav_hist ../sndfile-example-src/sample.wav side > side
```  
To get the histogram run `gnuplot` and `plot "side" with boxes`

<br>

### Exercise 3

**To remove 14 bits**
```bash
./wav_quant ../sndfile-example-src/sample.wav 2 sample_quant2b.wav
```
To check results listen to the audio file and use `wav_hist` to verify the quantization  

<br>

### Exercise 4

```bash
./wav_cmp ../sndfile-example-src/sample.wav sample_quant2b.wav
```

<br>

### Exercise 5

**To get a Single Echo with 2 of gain and 1s of delay**
```bash
./wav_effects ../sndfile-example-src/sample.wav single_echo 2 44100 single_echo_2_44100.wav
```
To check results listen to the audio file or use Audacity and see the waveform

<br>

**To get Multiple Echos with 0.8 of gain and 1s of delay**
```bash
./wav_effects ../sndfile-example-src/sample.wav multiple_echo 0.8 44100 multiple_echo_08_44100.wav
```
To check results listen to the audio file or use Audacity and see the waveform

<br>

**To get Amplitude Modulation with a signal of 2Hz**
```bash
./wav_effects ../sndfile-example-src/sample.wav amp_mod_2hz.wav amplitude_modulation 2
```
To check results listen to the audio file or use Audacity and see the waveform

<br>

**To get a reverse sample**
```bash
./wav_effects ../sndfile-example-src/sample.wav reverse.wav reverse
```
To check results listen to the audio file or do a reverse of reverse.wav and see that the audio files is equal to the original

### Exercise 7

**To decode a text file**
```bash
./decoder lusiadas.txt lusiadas_decoded.txt
```

<br>

**To encode a text file**
```bash
./decoder lusiadas_decoded.txt lusiadas_encoded.txt
```

### Exercise 8

**To encode an audio file using the codec**
```bash
./lossy_encoder ../sndfile-example-src/sample.wav lossy_encoded_sample 1024 819
```

<br>

**To decode a binary file containing the DCT coeficients**
```bash
./lossy_decoder lossy_encoded_sample lossy_decoded_sample.wav
```
