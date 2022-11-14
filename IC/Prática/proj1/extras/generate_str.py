import random
import string

def main(*argv):
    print(''.join(random.choices(string.ascii_letters, k=int(argv[1]))))

if __name__ == '__main__':
    import sys
    main(*sys.argv)
