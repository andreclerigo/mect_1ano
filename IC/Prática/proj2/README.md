# IC - Project 2

## Compile

Go to `proj2/opencv-src` and run `make all`

## Testing

Go to `proj2/opencv-bin`

<br>

### Exercise 1

```bash
./cp_image ../image_files/lena.ppm lena_copy.ppm
```  
To check the results either view the image by running the command with the word "view" or open the output file

<br>

### Exercise 2

**Negative image**
```bash
./negative_image ../image_files/monarch.ppm monarch_negative.ppm
```

**Mirror image in horizontal axis**
```bash
./mirror_image ../image_files/airplane.ppm airplane_h.ppm h
```

**Mirror image in vertical axis**
```bash
./mirror_image ../image_files/airplane.ppm airplane_v.ppm v
```

**Rotated image 90º**
```bash
./rotate_image ../image_files/arial.ppm arial_rot90.ppm 90
```

**Rotated image 180º**
```bash
./rotate_image ../image_files/arial.ppm arial_rot90.ppm 90
```

**Increase Light image**
```bash
./bright_image ../image_files/bike3.ppm bike3_h50.ppm 50
```

**Decrease Light image**
```bash
./bright_image ../image_files/bike3.ppm bike3_l50.ppm -50
```

To check the results either view the image by running the command with the word "view" or open the output file

<br>

### Exercise 4 and 5
#### That uses the Golomb class (golomb.h) developed in exercise 3
**Encode audio parameters**
```bash
./golomb_encoder <input file> <output file> <m | bs> [auto] [q]  
```  
<br>

**Encode audio lossless with fixed m**
```bash
./golomb_encoder ../audio_files/sample06.wav sample06_enc 256
```
**Encode audio lossy with fixed m**
```bash
./golomb_encoder ../audio_files/sample06.wav sample06_enc 256 4
```
When using a fixed m value, you must not use the "auto" parameter.  
<br>

**Encode audio lossless with dynamic m**
```bash
./golomb_encoder ../audio_files/sample06.wav sample06_enc 1024 auto
```
**Encode audio lossy with dynamic m**
```bash
./golomb_encoder ../audio_files/sample06.wav sample06_enc 1024 auto 8
```
When using a dynamic m value, you must indicate the blocksize and use the "auto" parameter.  
<br>

**Decode audio**
**Decode audio parameters**
```bash
./golomb_encoder <input file> <output file>
``` 
Example
```bash
./golomb_decoder sample06_enc sample06_decoded.wav
```

To run the tests script  
```bash
python3 automate.py
```

### Exercise 6
**Encode image parameters**
```bash
./image_encoder <intput_file> <output_file> [mode]
```
Mode it's an integer between 1 and 8.

**Decode image parameters**
```bash
./image_decoder <intput_file> <output_file>
```

To run the tests script  
```bash
python3 imageauto.py
```
