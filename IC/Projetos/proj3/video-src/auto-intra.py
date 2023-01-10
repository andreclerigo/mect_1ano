import subprocess

VIDEO_FILES = ['../video-files/garden_sif.y4m', '../video-files/students_qcif.y4m', '../video-files/akiyo_cif.y4m']     # 3 values
BS_VALUES = [16, 32, 64, 128, 256, 512]               # 7 values
idx = 0


def encoder(e, idx):
    times = []
    sizes = []
    warnings = []

    # Testing with fixed M values
    e.write('Testing with fixed BS values (16, 32, 64, 128, 256, 512)\n')
    for video in VIDEO_FILES:
        e.write(video + '\n')
        for bs in BS_VALUES:
            time = subprocess.check_output(f'../video-bin/intraframe_encoder {video} {idx} {bs}', shell=True)

            if "Warning" in time.decode("utf-8").split("\n")[0]:
                warnings.append("using " + time.decode("utf-8").split("\n")[0].split(" ")[-1] + " instead of " + str(bs) + " for bs")

            times.append(time.decode("utf-8").split("\n")[-2].split(" ")[-2])

            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            sizes.append(size.decode("utf-8").split(" ")[4])

            idx += 1

        dump_stats(e, times, sizes)
        dump_warnings(e, warnings)
        times.clear()
        sizes.clear()
        warnings.clear()
    
    return idx


def dump_warnings(f, warnings):
    f.write('Warnings:\n')
    for w in warnings:
        f.write(w + '\n')


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
        
        output = subprocess.check_output(f'../video-bin/intraframe_decoder {i} {i}.y4m', shell=True)
        output = output.decode("utf-8").split("\n")[-2].split(" ")[-2]
        d.write(f'{output}\n')

        # Remove all temporary files
        subprocess.call(f'rm {i}', shell=True)
        subprocess.call(f'rm {i}.y4m', shell=True)

    return idx


e = open('../video-bin/intra_encoder.txt', 'w')
d = open('../video-bin/intra_decoder.txt', 'w')

e.write('[Intraframe Encoder]\n\n')
print('Testing Intraframe Encoder')
idx = encoder(e, idx)

d.write('[Testing Decoder]\n\n')
print('Testing Decoder')
idx = decoder(d, idx)

e.close()
d.close()
