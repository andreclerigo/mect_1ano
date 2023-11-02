#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include <math.h>
#include "mq5.h"
#include "driver/adc.h"

const char *TAG = "MQ5";


/**
 * @brief Calibrate the sensor, this function should be running for several minutes after preheating for 24hours
 * 
 * @param device the mq5 structure data
 */
static void mq5_calibrate(mq5_t *device)
{
  ESP_LOGI(TAG, "Calibrating, please wait");
  float calcR0 = 0;
  for (int i = 0; i < 50; i++) {
      uint32_t voltage = adc1_get_raw(device->channel);
      float voltage_sensor = voltage * VOLTAGE_RANGE / ((float)pow(2, device->width) - 1);
      float resistance_sensor = ((VOLTAGE_RANGE * device->rl) / voltage_sensor) - device->rl;

      calcR0 += resistance_sensor / RATIO_MQ5_CLEAN_AIR;
  }
  device->r0 = calcR0 / 50;
  ESP_LOGI(TAG, "Calibration done!");
}

/**
 * @brief The mq5_init function initializes the mq5 sensor
 * 
 * @param device the mq5 structure data
 * @param channel the ADC channel of ESP32
 * @param width the ADC width of ESP32
 * @param atten the attenuation of ADC signal
 * @param a value of a in the equation y = a * x^b
 * @param b value of b in the equation y = a * x^b
 * @param rl load resistance value (potentiometer)
 */
void mq5_init(mq5_t *device, int channel, adc_bits_width_t width, adc_atten_t atten, float a, float b, float rl)
{
    adc1_channel_t c = (adc1_channel_t) channel;

    device->channel = c;
    device->width = width;
    device->atten = atten;
    device->a = a;
    device->b = b;
    device->rl = rl;

    adc1_config_width(device->width);
    adc1_config_channel_atten(device->channel, device->atten);

    mq5_calibrate(device);
}

/**
 * @brief Get the ratio value Rs/R0
 * 
 * @param device the mq5 structure data
 * @return float the ratio value
 */
float mq5_read_ratio(mq5_t *device)
{
    uint32_t voltage = adc1_get_raw(device->channel);
    float voltage_sensor = voltage * VOLTAGE_RANGE / ((float)pow(2, device->width) - 1);
    float resistance_sensor = ((VOLTAGE_RANGE * device->rl) / voltage_sensor) - device->rl;
    float ratio = resistance_sensor / device->r0;

    return ratio;
}

/**
 * @brief Get the ppm value
 * 
 * @param device the mq5 structure data
 * @param ratio the ratio value
 * @return float the ppm value
 */
float mq5_read(mq5_t *device, float ratio)
{
    return device->a * pow(ratio, device->b);
}
