import requests
import time
import random

while True:
    temperature = random.uniform(20, 28)  # Generate a random temperature between 15 and 30
    humidity = random.uniform(30, 40)  # Generate a random humidity between 30 and 50

    data = {
        'temperature': temperature.__round__(1),
        'humidity': humidity.__round__(1),
        'mq5': random.randint(0, 10000)
    }

    response = requests.post('http://localhost:5000/data', json=data)

    if response.status_code == 200:
        print('Data sent')
    else:
        print('Failed to send dht11 data', response.text)

    time.sleep(3)  # Wait for 3 seconds
