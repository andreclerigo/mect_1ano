import requests
import time
import random
import argparse


def main(rtt_min, rtt_avg, rtt_max, rtt_mdev, jitter, pdr):
    # Generate random data
    rtt_min = round(random.uniform(0.2, 0.5), 2) if rtt_min is None else rtt_min
    rtt_avg = round(random.uniform(0.8, 2.2), 2) if rtt_avg is None else rtt_avg
    rtt_max = round(random.uniform(2.5, 3.1), 2) if rtt_max is None else rtt_max
    rtt_mdev = round(random.uniform(0.2, 3.1), 2) if rtt_mdev is None else rtt_mdev
    jitter = round(random.uniform(0.2, 1.2), 2) if jitter is None else jitter
    pdr = round(random.uniform(80, 100), 2) if pdr is None else pdr

    data = {
        "rtt_min": rtt_min,
        "rtt_avg": rtt_avg,
        "rtt_max": rtt_max,
        "rtt_mdev": rtt_mdev,       
        "jitter": jitter,
        "pdr": pdr
    }

    response = requests.post('http://localhost:5000/batman', json=data)

    if response.status_code == 200:
        print('Data sent\n', data)
    else:
        print('Failed to send dht11 data', response.text)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Send data to server')
    parser.add_argument('--rtt_min', type=float, help='RTT min value')
    parser.add_argument('--rtt_avg', type=float, help='RTT avg value')
    parser.add_argument('--rtt_max', type=float, help='RTT max value')
    parser.add_argument('--rtt_mdev', type=float, help='RTT mdev value')
    parser.add_argument('--jitter', type=float, help='Jitter value')
    parser.add_argument('--pdr', type=float, help='PDR value')
    args = parser.parse_args()

    main(args.rtt_min, args.rtt_avg, args.rtt_max, args.rtt_mdev, args.jitter, args.pdr)
