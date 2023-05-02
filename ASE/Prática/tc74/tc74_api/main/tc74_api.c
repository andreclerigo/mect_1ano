#include "temp_sensor_TC74.h"
#include <stdio.h>
#include "esp_log.h"
#include "driver/i2c.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_timer.h"

#define I2C_MASTER_SCL_IO           15
#define I2C_MASTER_SDA_IO           13
#define I2C_MASTER_FREQ_HZ          50000
#define TC74_SENSOR_ADDR            0x4D

static const char *TAG = "TC74_API";
static i2c_port_t i2c_port = I2C_NUM_0;

void read_temperature_task(void *param)
{
    TickType_t previousWakeTime;

    while (1) {
        previousWakeTime = xTaskGetTickCount();
        int32_t temperature_sum = 0;
        uint8_t temperature = 0;
        int num_readings = 3;

        // Wake up and do the first read
        esp_err_t err = tc74_wakeup_and_read_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature);
        if (err == ESP_OK) {
            temperature_sum += (int8_t)temperature;
            ESP_LOGI(TAG, "0 Temperature: %d°C", (int8_t)temperature);
        } else {
            ESP_LOGE(TAG, "Failed to read temperature: %d", err);
        }
        vTaskDelay(pdMS_TO_TICKS(100)); // Delay for 100 ms between readings

        // Read the temperature 2 more times
        for (int i = 0; i < 2; i++) {
            temperature = 0;
            esp_err_t err = tc74_read_temp_after_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature);

            if (err == ESP_OK) {
                temperature_sum += (int8_t)temperature;
                ESP_LOGI(TAG, "%d Temperature: %d°C", i+1, (int8_t)temperature);
            } else {
                ESP_LOGE(TAG, "Failed to read temperature: %d", err);
            }

            vTaskDelay(pdMS_TO_TICKS(100)); // Delay for 100 ms between readings
        }

        int8_t average_temperature = temperature_sum / num_readings;
        ESP_LOGI(TAG, "Average Temperature: %d°C", average_temperature);

        // Put the sensor into standby mode
        esp_err_t standby_result = tc74_standby(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100));
        if (standby_result != ESP_OK) {
            ESP_LOGE(TAG, "Failed to put sensor into standby mode: %d", standby_result);
        }

        vTaskDelayUntil(&previousWakeTime, pdMS_TO_TICKS(5000));
    }
}

void temperature_timer_callback(void *arg)
{
    xTaskCreate(read_temperature_task, "Read Temperature", 2048, NULL, 5, NULL);
}


void app_main(void)
{
    esp_err_t init_result = tc74_init(i2c_port, I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO, I2C_MASTER_FREQ_HZ);
    ESP_LOGI("TC74", "Init result: %d", init_result);

    if (init_result == ESP_OK) {
        xTaskCreate(read_temperature_task, "Read Temperature", 2048, NULL, 5, NULL);
    }
}


// Main app to test if the standby works
/*
void app_main(void)
{
    i2c_port_t i2c_port = I2C_NUM_0;

    esp_err_t init_result = tc74_init(i2c_port, I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO, I2C_MASTER_FREQ_HZ);
    ESP_LOGI("TC74", "Init result: %d", init_result);

    if (init_result == ESP_OK) {
        uint8_t temperature_before = 0;
        esp_err_t err = tc74_wakeup_and_read_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature_before);
        if (err == ESP_OK) {
            ESP_LOGI(TAG, "Temperature before standby: %d°C", (int8_t)temperature_before);
        } else {
            ESP_LOGE(TAG, "Failed to read temperature before standby: %d", err);
        }

        // Put the sensor into standby mode
        esp_err_t standby_result = tc74_standby(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100));
        ESP_LOGI("TC74", "Standby result: %d", standby_result);

        if (standby_result == ESP_OK) {
            uint8_t temperature_after = 0;
            // Try to read temperature while the sensor is in standby mode
            err = tc74_read_temp_after_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature_after);

            if (err != ESP_OK) {
                ESP_LOGE(TAG, "Failed to read temperature after standby: %d", err);
            } else {
                ESP_LOGI(TAG, "Temperature after standby: %d°C", (int8_t)temperature_after);
                if (temperature_after == 64) {
                    ESP_LOGI(TAG, "Standby mode is working");
                } else {
                    ESP_LOGW(TAG, "Standby mode is not be working");
                }
            }
        }

        temperature_before = 0;
        err = tc74_wakeup_and_read_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature_before);
        if (err == ESP_OK) {
            ESP_LOGI(TAG, "Temperature after waking up: %d°C", (int8_t)temperature_before);
        } else {
            ESP_LOGE(TAG, "Failed to read temperature before standby: %d", err);
        }
    }
}
*/
