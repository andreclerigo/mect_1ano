#include <stdio.h>
#include <time.h>
#include "esp_log.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "spiffs.h"

// SPIFFS Configuration
#define SPIFFS_TEMP_PATH    "/spiffs/temp.txt"
#define SPIFFS_HUMID_PATH   "/spiffs/humid.txt"
#define SPIFFS_GAS_PATH     "/spiffs/gas.txt"

void app_main(void)
{
    static const char *TAG = "ANALYSE_SPIFFS";
    
    esp_vfs_spiffs_conf_t conf = {
      .base_path = "/spiffs",
      .partition_label = NULL,
      .max_files = 5,
      .format_if_mount_failed = true
    };
    
    // Initialize SPIFFS
    esp_err_t spiffs_result = spiffs_mount(&conf);
    if (spiffs_result == ESP_OK) {
        ESP_LOGI(TAG, "SPIFFS mounted successfully");
    } else {
        return;
    }
    spiffs_get_info(&conf);

    // Check SPIFFS partition for errors
    // spiffs_check(&conf);

    // Wait for 10 seconds before starting analysis process
    for (int i = 0; i < 10; i++) {
        ESP_LOGI(TAG, "%d seconds until analysis process begins", 10 - i);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }

    // Read files
    spiffs_read_file(SPIFFS_TEMP_PATH);
    spiffs_read_file(SPIFFS_HUMID_PATH);
    spiffs_read_file(SPIFFS_GAS_PATH);

    // // Delete files
    // spiffs_delete_file(SPIFFS_TEMP_PATH);
    // spiffs_delete_file(SPIFFS_HUMID_PATH);
    // spiffs_delete_file(SPIFFS_GAS_PATH);

    // // Check if files were deleted
    // if (spiffs_file_exists(SPIFFS_TEMP_PATH)) {
    //     ESP_LOGE(TAG, "File still exists after deletion");
    // } else {
    //     ESP_LOGI(TAG, "File successfully deleted");
    // }

    // if (spiffs_file_exists(SPIFFS_HUMID_PATH)) {
    //     ESP_LOGE(TAG, "File still exists after deletion");
    // } else {
    //     ESP_LOGI(TAG, "File successfully deleted");
    // }

    // if (spiffs_file_exists(SPIFFS_GAS_PATH)) {
    //     ESP_LOGE(TAG, "File still exists after deletion");
    // } else {
    //     ESP_LOGI(TAG, "File successfully deleted");
    // }

    // Unmount SPIFFS
    spiffs_result = spiffs_unmount(&conf);
    if (spiffs_result == ESP_OK) {
        ESP_LOGI(TAG, "SPIFFS unmounted successfully");
    } else {
        ESP_LOGE(TAG, "SPIFFS unmount failed");
        return;
    }
}
