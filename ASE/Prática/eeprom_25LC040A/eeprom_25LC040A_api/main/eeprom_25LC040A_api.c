#include "spi_25LC040A_eeprom.h"
#include <stdio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"

#define CS_PIN 5
#define SCK_PIN 18
#define MOSI_PIN 23
#define MISO_PIN 19
#define CLK_SPEED_HZ 1000000

static const char *TAG = "25LC040A_eeprom_api";

spi_device_handle_t spi_device;

void app_main(void)
{
    // Initialize SPI device
    spi_host_device_t masterHostId = VSPI_HOST;
    esp_err_t ret = spi_25LC040_init(masterHostId, CS_PIN, SCK_PIN, MOSI_PIN, MISO_PIN, CLK_SPEED_HZ, &spi_device);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize SPI device: %d", ret);
        return;
    }
    ESP_LOGI(TAG, "SPI device initialized successfully");

    // Enable write
    ret = spi_25LC040_write_enable(spi_device);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write enable");
        return;
    }

    ret = spi_25LC040_write_status(spi_device, 0x00); 
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write in status");
        return;
    }
    
    // Commands WRITE and READ from STATUS
    /*while (1) {
        ret = spi_25LC040_write_status(spi_device, 0x0C); 
        if (ret != ESP_OK) {
            ESP_LOGE(TAG, "Failed to write in status");
            return;
        }
        vTaskDelay(pdMS_TO_TICKS(10));

        uint8_t status;
        ret = spi_25LC040_read_status(spi_device, &status);
        if (ret != ESP_OK) {
            ESP_LOGE(TAG, "Failed to read status");
            return;
        }
        ESP_LOGI(TAG, "Status: 0x%02X", status);

        vTaskDelay(pdMS_TO_TICKS(10));
    }*/

    
    // Commands WRITE and READ from EEPROM
    /*while (1) {
        ret = spi_25LC040_write_byte(spi_device, 0x00, 0x01);
        if (ret != ESP_OK) {
            ESP_LOGE(TAG, "Failed to write byte");
            return;
        }
        vTaskDelay(pdMS_TO_TICKS(10));

        uint8_t data[1];
        ret = spi_25LC040_read_byte(spi_device, 0x00, data);
        if (ret != ESP_OK) {
            ESP_LOGE(TAG, "Failed to read byte");
            return;
        }
        ESP_LOGI(TAG, "Read data: 0x%02X", data[0]);

        vTaskDelay(pdMS_TO_TICKS(10));
    }
    */
   
    // Commands WRITE PAGE and READ from EEPROM
    /*
    uint8_t page_data[16] = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x10, 0x11, 0x12, 0x13, 0x14, 0x21, 0x31, 0x41, 0x55 };
    spi_25LC040_write_page(spi_device, 0x0000, page_data, 16);
    vTaskDelay(pdMS_TO_TICKS(10));

    uint8_t data_read[1];
    uint8_t address = 0x00;
    for (uint8_t i  = 0; i < 16;i++) {
        spi_25LC040_read_byte(spi_device, address, data_read);
        ESP_LOGI(TAG, "Data read from memory 0x%x: 0x%x", address, data_read[0]);
        address++;
    }

    uint8_t page_data2[16] = { 0xFF, 0xF1, 0xDD, 0xC1, 0xCC, 0xBB, 0xB1, 0xA1, 0xAA, 0xC1, 0xCC, 0xDE, 0xCF, 0xAD, 0xDA, 0xBF };
    spi_25LC040_write_page(spi_device, 0x0010, page_data2, 16);
    address = 0x10;
    for (uint8_t i  = 0; i < 16;i++) {
        spi_25LC040_read_byte(spi_device, address, data_read);
        ESP_LOGI(TAG, "Data read from memory 0x%x: 0x%x", address, data_read[0]);
        address++;
    }
    */

    // Disable write
    ret = spi_25LC040_write_disable(spi_device);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write disable");
        return;
    }

    // Free SPI device
    esp_err_t free_result = spi_25LC040_free(masterHostId, spi_device);
    if (free_result == ESP_OK) {
        ESP_LOGI(TAG, "SPI device freed succeeded");
    } else {
        ESP_LOGI(TAG, "SPI devied failed free");
    }
}
