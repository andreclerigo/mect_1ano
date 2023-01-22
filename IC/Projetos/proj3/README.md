# IC - Project 3

## Compile

Go to `proj3/video-src` and execute `make all`

## Testing

Go to `proj3/video-bin`

<br>

### Exercise 1

To encode the file: <br>
```bash
./intraframe_encoder <intput_file> <output_file> <bs>
```
And then to decode the file: <br>
```bash
./intraframe_encoder <intput_file> <output_file>
```

<br>

### Exercise 2

To encode the file: <br>
```bash
./interframe_encoder <intput_file> <output_file> <bs> <search area> <key-frame period>
```
And then to decode the file: <br>
```bash
./interframe_encoder <intput_file> <output_file>
```

<br>

### Exercise 3

**Intra-frame lossy codec**<br>
To encode the file: <br>
```bash
./lossy_intra_encoder <input file> <output file> <block size> <quantization>
```
And then to decode the file: <br>
```bash
./lossy_intra_decoder <input file> <output file>
```

**Inter-frame lossy codec**<br>
To encode the file: <br>
```bash
./lossy_inter_encoder <input file> <output file> <block size> <search area> <key-frame period> <quantization>
```
And then to decode the file: <br>
```bash
./lossy_inter_decoder <input file> <output file>
```

**Video comparing program** <br>
In order to assess the quality of the lossy compressed video sequences in terms of the peak signal to noise ratio (PSNR): <br>
```bash
./video_cmp <original input file> <reconstructed input file>
```
<br>

# Authors
[André Clérigo](https://github.com/andreclerigo), 98485   
[João Amaral](https://github.com/jp-amaral), 98373  
[Pedro Rocha](https://github.com/PedroRocha9), 98256  

# Grade
18,4
