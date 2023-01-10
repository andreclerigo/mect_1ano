import subprocess

VIDEO_FILES = ['../video-files/garden_sif.y4m', '../video-files/students_qcif.y4m', '../video-files/akiyo_cif.y4m']     # 3 values
BS_VALUES = [16, 32, 64, 128]                       # 4 values
AREA_VALUES = [4, 8, 16, 32]                        # 4 values
KEYFRAME_VALUES = [3, 5, 10, 15]                    # 4 values
idx = 0


def encoder(e, idx):
    times = []
    sizes = []

    # Testing with fixed M values
    e.write('Testing with BS, AREA AND KEYFRAME\n')
    for video in VIDEO_FILES:
        e.write(video + '\n')
        for kf in KEYFRAME_VALUES:
            for area in AREA_VALUES:
                for bs in BS_VALUES:
                    try:
                        time = subprocess.check_output(f'../video-bin/interframe_encoder {video} {idx} {bs} {area} {kf}', shell=True)
                        times.append(time.decode("utf-8").split("\n")[-2].split(" ")[-2])

                        size = subprocess.check_output(f'ls -l {idx}', shell=True)
                        sizes.append(size.decode("utf-8").split(" ")[4])
                    except:
                        times.append(-1)
                        sizes.append(-1)

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
    for i in range(idx):
        # Decode lossless
        if i % len(BS_VALUES) == 0:
            d.write(VIDEO_FILES[i //  len(BS_VALUES) % len(VIDEO_FILES)] + '\n')
        
        try:
            output = subprocess.check_output(f'../video-bin/interframe_decoder {i} {i}.y4m', shell=True)
            output = output.decode("utf-8").split("\n")[-2].split(" ")[-2]
            d.write(f'{output}\n')
            
        except:
            d.write('-1\n')

        # Remove all temporary files
        subprocess.call(f'rm {i}', shell=True)
        subprocess.call(f'rm {i}.y4m', shell=True)

    return idx


e = open('../video-bin/inter_encoder.txt', 'w')
d = open('../video-bin/inter_decoder.txt', 'w')

e.write('[Interframe Encoder]\n\n')
print('Testing Interframe Encoder')
idx = encoder(e, idx)

d.write('[Testing Decoder]\n\n')
print('Testing Decoder')
idx = decoder(d, idx)

e.close()
d.close()
