import subprocess

AUDIO_FILES = ['../audio_files/sample04.wav', '../audio_files/sample06.wav', '../audio_files/sample01.wav']     # 3 values
FIXED_M_VALUES = [128, 256, 512, 1024, 2048]            # 5 values
BS_VALUES = [128, 512, 1024, 2048, 16384]               # 5 values
QUANT_VALUES = [2, 4, 6, 8]                             # 4 values
idx = 0


def lossy_encoder(loe, idx):
    times = []
    sizes = []
    
    loe.write('Testing with auto M diffrent block 2048\n')
    loe.write('Testing quantization values (2, 4, 6, 8)\n')
    for audio in AUDIO_FILES:
        loe.write(audio + '\n')
        for q in QUANT_VALUES:
            time = subprocess.check_output(f'../opencv-bin/golomb_encoder {audio} {idx} 2048 auto {q}', shell=True)
            times.append(time.decode("utf-8").split(" ")[2])
            
            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            #loe.write(f'{size.decode("utf-8").split(" ")[4]}\n')
            sizes.append(size.decode("utf-8").split(" ")[4])

            idx += 1

        dump_stats(loe, times, sizes)
        times.clear()
        sizes.clear()
    
    return idx


def lossless_encoder(lle, idx):
    times = []
    sizes = []

    # Testing with fixed M values
    lle.write('Testing with fixed M values (128, 256, 512, 1024, 2048)\n')
    for audio in AUDIO_FILES:
        lle.write(audio + '\n')
        for m in FIXED_M_VALUES:
            time = subprocess.check_output(f'../opencv-bin/golomb_encoder {audio} {idx} {m}', shell=True)
            times.append(time.decode("utf-8").split(" ")[2])

            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            sizes.append(size.decode("utf-8").split(" ")[4])
            
            idx += 1
        
        dump_stats(lle, times, sizes)
        times.clear()
        sizes.clear()

    # Testing with auto M and diffrent block sizes
    lle.write('\nAuto M and diffrent block sizes (128, 512, 1024, 2048, 16384)\n')
    for audio in AUDIO_FILES:
        lle.write(audio + '\n')
        for bs in BS_VALUES:
            time = subprocess.check_output(f'../opencv-bin/golomb_encoder {audio} {idx} {bs} auto', shell=True)
            times.append(time.decode("utf-8").split(" ")[2])

            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            sizes.append(size.decode("utf-8").split(" ")[4])
            
            idx += 1

        dump_stats(lle, times, sizes)
        times.clear()
        sizes.clear()
    
    return idx


def dump_stats(f, times=None, sizes=None, snr=None):
    f.write('times:\n')
    for t in times:
        f.write(f'{t}\n')

    f.write('sizes:\n')
    for s in sizes:
        f.write(f'{s}\n')


def decoder(d, idx):
    snrs = []

    for i in range(idx):
        if i < 30:
            # Decode lossless
            if i % 5 == 0:
                if i == 0:
                    d.write('Testing with fixed M values (128, 256, 512, 1024, 2048)\n')
                if i == 15:
                    d.write('\nAuto M and diffrent block sizes (128, 512, 1024, 2048, 16384)\n')
                d.write(AUDIO_FILES[i // 5 % 3] + '\n')
            
            output = subprocess.check_output(f'../opencv-bin/golomb_decoder {i} {i}.wav', shell=True)
            d.write(f'{output.decode("utf-8").split(" ")[2]}\n')
        else:
            # Decode lossy
            if (i-30) % 4 == 0:
                if i == 30:
                    d.write('\nTesting with auto M diffrent block 2048\n')
                    d.write('Testing quantization values (2, 4, 6, 8)\n')
                d.write(AUDIO_FILES[(i-30) // 4 % 3] + '\n')

            output = subprocess.check_output(f'../opencv-bin/golomb_decoder {i} {i}.wav', shell=True)
            snr = subprocess.check_output(f'../../proj1/sndfile-example-bin/wav_cmp {AUDIO_FILES[(i-30) // 4 % 3]} {i}.wav', shell=True)
            #d.write(f'{snr.decode("utf-8").split(" ")[1]}\n')
            d.write(f'{output.decode("utf-8").split(" ")[2]}\n')
            snrs.append(snr.decode("utf-8").split(" ")[1])

        # Remove all temporary files
        subprocess.call(f'rm {i}', shell=True)
        subprocess.call(f'rm {i}.wav', shell=True)

    d.write('\nSNR:\n')
    for i, s in enumerate(snrs):
        if i % 4 == 0:
            d.write(f'{AUDIO_FILES[i // 4 % 3]}\n')
        d.write(f'{s}\n')

    d.write('\nSNR Wav_quant:\n')
    for audio in AUDIO_FILES:
        d.write(audio + '\n')
        for q in QUANT_VALUES:
            subprocess.check_output(f'../../proj1/sndfile-example-bin/wav_quant {audio} {16-q} x.wav', shell=True)
            snr = subprocess.check_output(f'../../proj1/sndfile-example-bin/wav_cmp {audio} x.wav', shell=True)
            d.write(f'{snr.decode("utf-8").split(" ")[1]}\n')

        subprocess.call(f'rm x.wav', shell=True)
    
    return idx


lle = open('../opencv-bin/lossless_encoder.txt', 'w')
d = open('../opencv-bin/decoder.txt', 'w')
loe = open('../opencv-bin/lossy_encoder.txt', 'w')

lle.write('[Lossless Encoder]\n\n')
print('Testing Lossless Encoder')
idx = lossless_encoder(lle, idx)

loe.write('[Lossy Encoder]\n\n')
print('Testing Lossy Encoder')
idx = lossy_encoder(loe, idx)

d.write('[Testing Decoder]\n\n')
print('Testing Decoder')
idx = decoder(d, idx)

lle.close()
loe.close()
d.close()
