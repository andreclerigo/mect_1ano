#include "esp_timer.h"
#include "driver/gpio.h"
#include "rom/ets_sys.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "dht11.h"

static gpio_num_t dht_gpio;
static int64_t last_read_time = -2000000;


/**
 * @brief Wait for a level to change.
 * 
 * @param microSeconds the maximum time to wait for the level to change
 * @param level the level to wait for (0 = low, 1 = high)
 * @return int the amount of microseconds waited, or DHT11_TIMEOUT_ERROR if no change
 */
static int _waitForLevel(uint16_t microSeconds, int level)
{
    int ticks = 0;
    while (gpio_get_level(dht_gpio) == level) { 
        if (ticks++ > microSeconds) 
            return DHT11_TIMEOUT_ERROR;
        ets_delay_us(1);
    }
    return ticks;
}

/**
 * @brief Check the CRC checksum of the data received from the DHT11.
 * 
 * @param data the data received from the DHT11
 * @return int DHT11_OK if the checksum is correct, DHT11_CRC_ERROR if not
 */
static int _checkCRC(uint8_t data[])
{
    if (data[4] == (data[0] + data[1] + data[2] + data[3])) {
        return DHT11_OK;
    } else {
        return DHT11_CRC_ERROR;
    }
}

/**
 * @brief MCU Sends out Start Signal to DHT11.
 * Data Single-bus free status is at high voltage level. When the communication between MCU and
 * DHT11 begins, the programme of MCU will set Data Single-bus voltage level from high to low
 * and this process must take at least 18ms to ensure DHT’s detection of MCU's signal, then MCU
 * will pull up voltage and wait 20-40us for DHT’s response.
 */
static void _sendStartSignal()
{
    gpio_set_direction(dht_gpio, GPIO_MODE_OUTPUT);
    gpio_set_level(dht_gpio, 0);
    // Set Data Single-bus voltage level to low for at least 18ms to ensure DHT’s detection
    vTaskDelay(20 / portTICK_PERIOD_MS);
    gpio_set_level(dht_gpio, 1);
    // Pull up voltage and wait 20-40us for DHT’s response
    ets_delay_us(40);
    gpio_set_direction(dht_gpio, GPIO_MODE_INPUT);
}

/**
 * @brief  DHT Responses to MCU.
 * Once DHT detects the start signal, it will send out a low-voltage-level response signal, which
 * lasts 80us. Then the programme of DHT sets Data Single-bus voltage level from low to high and
 * keeps it for 80us for DHT’s preparation for sending data.
 * When DATA Single-Bus is at the low voltage level, this means that DHT is sending the response
 * signal. Once DHT sent out the response signal, it pulls up voltage and keeps it for 80us and
 * prepares for data transmission.
 * 
 * @return int dht11_data_t's status bit
 */
static int _checkResponse()
{
    // Wait for 80us
    if(_waitForLevel(80, 0) == DHT11_TIMEOUT_ERROR)
        return DHT11_TIMEOUT_ERROR;

    // Wait for 80us
    if(_waitForLevel(80, 1) == DHT11_TIMEOUT_ERROR) 
        return DHT11_TIMEOUT_ERROR;

    return DHT11_OK;
}

/**
 * @brief Construct a new dht11_data_t object with timeout error.
 * 
 * @return struct dht11_data_t 
 */
static struct dht11_data_t _timeoutError()
{
    struct dht11_data_t timeoutError = {DHT11_TIMEOUT_ERROR, -1, -1, -1, -1};
    return timeoutError;
}

/**
 * @brief Construct a new dht11_data_t object with CRC error.
 * 
 * @return struct dht11_data_t 
 */
static struct dht11_data_t _crcError()
{
    struct dht11_data_t crcError = {DHT11_CRC_ERROR, -1, -1, -1, -1};
    return crcError;
}

/**
 * @brief Initialize the DHT11 sensor.
 * 
 * @param gpio_num the GPIO pin the DHT11 is connected to
 */
void dht11_init(gpio_num_t gpio_num) {
    // Wait for device to pass the initial unstable state
    vTaskDelay(1000 / portTICK_PERIOD_MS);
    dht_gpio = gpio_num;
}

/**
 * @brief DHT11 Sends data to MCU.
 * When DHT is sending data to MCU, every bit of data begins with the 50us low-voltage-level and
 * the length of the following high-voltage-level signal determines whether data bit is "0" or "1".
 * If the high-voltage-level signal lasts 26-28us, it indicates bit "0"; and if the high-voltage-level
 * signal lasts 70us, it indicates bit "1".
 * 
 * @return struct dht11_data_t 
 */
esp_err_t dht11_read(struct dht11_data_t *data)
{
    // Sensing too soon since last read (didn't wait for 2 seconds to make a new read)
    if (esp_timer_get_time() - 2000000 < last_read_time) {
        return ESP_FAIL;
    }
    last_read_time = esp_timer_get_time();

    uint8_t tempData[5] = {0, 0, 0, 0, 0};

    // Send the start signal to the DHT11
    _sendStartSignal();

    // Wait for the DHT11 to respond
    if (_checkResponse() == DHT11_TIMEOUT_ERROR) {
        *data = _timeoutError();
        return ESP_FAIL;
    }
    
    // Read transmitted data
    for (int i = 0; i < 40; i++) {
        // Start bit, wait for 50us low voltage level
        if (_waitForLevel(50, 0) == DHT11_TIMEOUT_ERROR) {
            *data = _timeoutError();
            return ESP_FAIL;
        }

        // Check received bit duration
        if (_waitForLevel(70, 1) > 28) {
            // Bit received was a 1
            tempData[i/8] |= (1 << (7-(i%8)));
        }
    }

    // Check checksum CRC
    if (_checkCRC(tempData) != DHT11_CRC_ERROR) {
        data->status = DHT11_OK;
        data->humidity = tempData[0];
        data->humidity_decimal = tempData[1];
        data->temperature = tempData[2];
        data->temperature_decimal = tempData[3];
        return ESP_OK;
    } else {
        *data = _crcError();
        return ESP_FAIL;
    }
}
