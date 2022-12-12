import subprocess

IMAGE_FILES = ['../image-files/arial.ppm', '../image-files/bike3.ppm', '../image-files/anemone.ppm', '../image-files/house.ppm']     # 4 values
MODE_VALUES = [1, 2, 3, 4, 5, 6, 7, 8]               # 8 values
idx = 0

def lossless_encoder(lle, idx):
    times = []
    sizes = []

    # Testing with fixed M values
    lle.write('Testing with auto BS and different modes (1,2,3,4,5,6,7,8)\n')
    for image in IMAGE_FILES:
        lle.write(image +  ': ' + str((subprocess.check_output(f'ls -l {image}', shell=True)).decode("utf-8").split(" ")[4]) + '\n')
        for m in MODE_VALUES:
            time = subprocess.check_output(f'../opencv-bin/image_encoder {image} {idx} {m}', shell=True)
            times.append(time.decode("utf-8").split(" ")[2])

            size = subprocess.check_output(f'ls -l {idx}', shell=True)
            sizes.append(size.decode("utf-8").split(" ")[4])
            
            idx += 1
        
        dump_times_sizes(lle, times, sizes)
        times.clear()
        sizes.clear()

    return idx


def dump_times_sizes(f, times, sizes):
    f.write('times:\n')
    for t in times:
        f.write(f'{t}\n')

    f.write('sizes:\n')
    for s in sizes:
        f.write(f'{s}\n')


def decoder(d, idx):
    for i in range(idx):
        # Decode lossless
        if i % 8 == 0:
            d.write(IMAGE_FILES[i // 5 % 3] + '\n')
        
        output = subprocess.check_output(f'../opencv-bin/image_decoder {i} {i}.ppm', shell=True)
        d.write(f'{output.decode("utf-8").split(" ")[2]}\n')

        # Remove all temporary files
        subprocess.call(f'rm {i}', shell=True)
        subprocess.call(f'rm {i}.ppm', shell=True)

    return idx


lle = open('../opencv-bin/image_encoder.txt', 'w')
d = open('../opencv-bin/image_decoder.txt', 'w')

lle.write('[Image Encoder]\n\n')
print('Testing Image Encoder')
idx = lossless_encoder(lle, idx)


d.write('[Testing Image Decoder]\n\n')
print('Testing Image Decoder')
idx = decoder(d, idx)

lle.close()
d.close()
