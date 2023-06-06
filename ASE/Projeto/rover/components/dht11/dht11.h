#ifndef DHT11_H_
#define DHT11_H_

#include "driver/gpio.h"

enum dht11_status {
    DHT11_CRC_ERROR = -2,
    DHT11_TIMEOUT_ERROR = -1,
    DHT11_OK = 0
};

struct dht11_data_t {
    int status;
    uint8_t humidity;
    uint8_t humidity_decimal;       // This shouldn't be necessary
    uint8_t temperature;
    uint8_t temperature_decimal;
};

void dht11_init(gpio_num_t);
esp_err_t dht11_read(struct dht11_data_t *data);

#endif // DHT11_H_