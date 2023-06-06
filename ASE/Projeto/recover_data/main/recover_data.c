#include <stdio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "esp_system.h"
#include "eeprom_25LC040A.h"

// EEPROM Configuration
#define CS_PIN              CONFIG_EEPROM_CS_PIN
#define SCK_PIN             CONFIG_EEPROM_SCK_PIN
#define MOSI_PIN            CONFIG_EEPROM_MOSI_PIN
#define MISO_PIN            CONFIG_EEPROM_MISO_PIN
#define CLK_SPEED_HZ        CONFIG_EEPROM_CLK_SPEED_HZ

static const char *TAG = "RECOVER_DATA";
spi_device_handle_t spi_device;
spi_host_device_t masterHostId = VSPI_HOST;

void reset_eeprom()
{
    // Write in status register on E2PROM 25LC040A to enable writing on all memory
    esp_err_t ret = eeprom_write_status(spi_device, 0x00); 
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write in status");
        return;
    }

    for (int i = 0; i < 512; i++) {
        eeprom_write_byte(spi_device, i, 0);
        vTaskDelay(100 / portTICK_PERIOD_MS);
    }
}

void app_main(void)
{
    // Initialize SPI device E2PROM 25LC040A
    esp_err_t ret_eeprom = eeprom_init(masterHostId, CS_PIN, SCK_PIN, MOSI_PIN, MISO_PIN, CLK_SPEED_HZ, &spi_device);
    if (ret_eeprom != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize EEPROM device: %d", ret_eeprom);
        return;
    }
    ESP_LOGI(TAG, "EEPROM device initialized successfully");    

    // Wait for 10 seconds to recover data
    for (int i = 0; i < 10; i++) {
        ESP_LOGI(TAG, "%d seconds until recover process begins", 10 - i);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }

    uint8_t data_read[1];
    for (int i = 0; i < 512; i++) {
        eeprom_read_byte(spi_device, i, data_read);
        printf("Data read from memory 0x%x: %d\n", i, data_read[0]);
    }

    // Free SPI device E2PROM 25LC040A
    esp_err_t free_result = eeprom_free(masterHostId, spi_device);
    if (free_result == ESP_OK) {
        ESP_LOGI(TAG, "EEPROM device freed succeeded");
    } else {
        ESP_LOGI(TAG, "EEPROM devied failed free");
    }
}
