import subprocess

VIDEO_FILES = ['../video-files/garden_sif.y4m', '../video-files/students_qcif.y4m', '../video-files/akiyo_cif.y4m']     # 3 values
BITS = [1, 2, 3, 4, 5, 6]
BEST_BS = [352, 176, 352]
idx = 0


def encoder(e, idx):
    times = []
    sizes = []

    # Testing with fixed M values
    e.write('Testing with quantization for 1, 2, 3, 4, 5, 6 bits\n')
    for i, video in enumerate(VIDEO_FILES):
        e.write(video + '\n')
        for bit in BITS:
            time = subprocess.check_output(f'../video-bin/lossy_intra_encoder {video} {idx} {BEST_BS[i]} {bit}', shell=True)
            times.append(time.decode("utf-8").split("\n")[-2].split(" ")[-2])

            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            sizes.append(size.decode("utf-8").split(" ")[4])

            idx += 1

        dump_stats(e, times, sizes)
        times.clear()
        sizes.clear()
    
    return idx


def dump_stats(f, times=None, sizes=None):
    f.write('times:\n')
    for t in times:
        f.write(f'{t}\n')

    f.write('sizes:\n')
    for s in sizes:
        f.write(f'{s}\n')


def decoder(d, idx):
    snrs = []
    
    for i in range(idx):
        # Decode lossless
        if i % len(BITS) == 0:
            d.write(VIDEO_FILES[i //  len(BITS) % len(VIDEO_FILES)] + '\n')
        
        try:
            output = subprocess.check_output(f'../video-bin/lossy_intra_decoder {i} {i}.y4m', shell=True)
            output = output.decode("utf-8").split("\n")[-2].split(" ")[-2]
            d.write(f'{output}\n')
            
            snr = subprocess.check_output(f'../video-bin/video_cmp {VIDEO_FILES[i //  len(BITS) % len(VIDEO_FILES)]} {i}.y4m', shell=True)
            snrs.append(snr.decode("utf-8").split("\n")[-2].split(" ")[-1])
        except:
            d.write('-1\n')

        # Remove all temporary files
        subprocess.call(f'rm {i}', shell=True)
        subprocess.call(f'rm {i}.y4m', shell=True)

    d.write('\nSNR:\n')
    for i, s in enumerate(snrs):
        if i % len(BITS) == 0:
            d.write(f'{VIDEO_FILES[i //  len(BITS) % len(VIDEO_FILES)]}\n')
        d.write(f'{s}\n')

    return idx


e = open('../video-bin/lossy_intra_encoder.txt', 'w')
d = open('../video-bin/lossy_intra_decoder.txt', 'w')

e.write('[Lossy Intraframe Encoder]\n\n')
print('Testing Lossy Intraframe Encoder')
idx = encoder(e, idx)

d.write('[Testing Decoder]\n\n')
print('Testing Decoder')
idx = decoder(d, idx)

e.close()
d.close()
