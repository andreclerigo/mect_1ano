import random
import string

def main(*argv):
    print(''.join(random.choices('10', k=int(argv[1]))))

if __name__ == '__main__':
    import sys
    main(*sys.argv)
