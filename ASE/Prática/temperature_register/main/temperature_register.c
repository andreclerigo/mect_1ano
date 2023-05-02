#include <stdio.h>
#include "temp_sensor_TC74.h"
#include "spi_25LC040A_eeprom.h"
#include "driver/i2c.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "esp_timer.h"
#include "esp_system.h"

#define I2C_MASTER_SCL_IO   15
#define I2C_MASTER_SDA_IO   13
#define I2C_MASTER_FREQ_HZ  50000
#define TC74_SENSOR_ADDR    0x4D
#define CS_PIN              5
#define SCK_PIN             18
#define MOSI_PIN            23
#define MISO_PIN            19
#define CLK_SPEED_HZ        1000000

static const char *TAG = "TEMP_REGISTER";
static i2c_port_t i2c_port = I2C_NUM_0;
spi_device_handle_t spi_device;
spi_host_device_t masterHostId = VSPI_HOST;
TaskHandle_t xHandle = NULL;
bool end = false;


void read_temperature_task(void *param)
{
    esp_err_t ret_spi = spi_25LC040_init(masterHostId, CS_PIN, SCK_PIN, MOSI_PIN, MISO_PIN, CLK_SPEED_HZ, &spi_device);
    if (ret_spi != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize SPI device: %d", ret_spi);
        return;
    }
    ESP_LOGI(TAG, "SPI device initialized successfully");

    // Enable write
    esp_err_t ret = spi_25LC040_write_enable(spi_device);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write enable");
        return;
    }

    // Write in status register
    ret = spi_25LC040_write_status(spi_device, 0x00); 
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write in status");
        return;
    }

    uint8_t page_data[16];
    int address = 0;
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
            // ESP_LOGI(TAG, "0 Temperature: %d°C", (int8_t)temperature);
        } else {
            ESP_LOGE(TAG, "Failed to read temperature: %d", err);
        }
        vTaskDelay(pdMS_TO_TICKS(100)); // Delay for 100 ms between readings

        // Read the temperature 2 more times
        for (int i = 0; i < 2; i++) {
            temperature = 0;
            err = tc74_read_temp_after_temp(i2c_port, TC74_SENSOR_ADDR, pdMS_TO_TICKS(100), &temperature);

            if (err == ESP_OK) {
                temperature_sum += (int8_t)temperature;
                // ESP_LOGI(TAG, "%d Temperature: %d°C", i+1, (int8_t)temperature);
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
        
        // Write to EEPROM
        if (address % 16 && address != 0) {
            esp_err_t ret = spi_25LC040_write_page(spi_device, address - 15, page_data, 16);

            if (ret != ESP_OK) {
                ESP_LOGE(TAG, "Failed to write byte");
                return;
            }
        }
        // esp_err_t ret = spi_25LC040_write_byte(spi_device, address, page_data[address%16]);
        // if (ret != ESP_OK) {
        //     ESP_LOGE(TAG, "Failed to write byte");
        //     return;
        // }
        
        if (address == 511) end = true;
        
        page_data[address%16] = average_temperature;

        vTaskDelay(pdMS_TO_TICKS(100));

        // Disable write
        ret = spi_25LC040_write_disable(spi_device);
        if (ret != ESP_OK) {
            ESP_LOGE(TAG, "Failed to write disable");
            return;
        }
        
        address++;
        
        vTaskDelayUntil(&previousWakeTime, pdMS_TO_TICKS(5000));
    }
}

void app_main(void)
{
    // Initialize I2C device
    esp_err_t ret_i2c = tc74_init(i2c_port, I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO, I2C_MASTER_FREQ_HZ);
    if (ret_i2c != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize I2C device: %d", ret_i2c);
        return;
    }
    ESP_LOGI(TAG, "I2C device initialized successfully");
    
    
    if (ret_i2c == ESP_OK) {
        xTaskCreate(read_temperature_task, "Read Temp", 2048, NULL, 5, &xHandle);
    }

    while (end != true) {
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    vTaskDelete(xHandle);

    uint8_t data_read[1];
    for (int i = 0; i < 512; i++) {
        spi_25LC040_read_byte(spi_device, i, data_read);
        printf("Data read from memory 0x%x: %d\n", i, data_read[0]);
    }

    // Free SPI device
    esp_err_t free_result = spi_25LC040_free(masterHostId, spi_device);
    if (free_result == ESP_OK) {
        ESP_LOGI(TAG, "SPI device freed succeeded");
    } else {
        ESP_LOGI(TAG, "SPI devied failed free");
    }
}
