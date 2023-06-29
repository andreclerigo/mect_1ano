#include "esp_err.h"
#include "esp_log.h"
#include "esp_spiffs.h"
#include <stdio.h>
#include <string.h>
#include <sys/unistd.h>
#include <sys/stat.h>

static const char *TAG = "SPIFFS";

/**
 * @brief Mount SPIFFS partition
 * 
 * @param conf configuration struct for SPIFFS
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL/ESP_ERR_NOT_FOUND if it failed
 */
esp_err_t spiffs_mount(esp_vfs_spiffs_conf_t *conf)
{
    esp_err_t ret = esp_vfs_spiffs_register(conf);

    if (ret != ESP_OK) {
        if (ret == ESP_FAIL) {
            ESP_LOGE(TAG, "Failed to mount or format filesystem");
        } else if (ret == ESP_ERR_NOT_FOUND) {
            ESP_LOGE(TAG, "Failed to find SPIFFS partition");
        } else {
            ESP_LOGE(TAG, "Failed to mount SPIFFS (%s)", esp_err_to_name(ret));
        }
    }

    return ret;
}

/**
 * @brief Check SPIFFS partition for errors
 * 
 * @param conf configuration struct for SPIFFS
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_check(esp_vfs_spiffs_conf_t *conf)
{
    return esp_spiffs_check(conf->partition_label);
}

/**
 * @brief Format SPIFFS partition
 * 
 * @param conf configuration struct for SPIFFS
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_format(esp_vfs_spiffs_conf_t *conf)
{
    esp_err_t ret = esp_spiffs_format(conf->partition_label);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to format SPIFFS (%s)", esp_err_to_name(ret));
    }

    return ret;
}

/**
 * @brief Get information about SPIFFS partition
 * 
 * @param conf configuration struct for SPIFFS
 */
void spiffs_get_info(esp_vfs_spiffs_conf_t *conf)
{
    size_t total = 0, used = 0;
    esp_err_t ret = esp_spiffs_info(conf->partition_label, &total, &used);

    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to get SPIFFS partition information (%s). Formatting...", esp_err_to_name(ret));
        spiffs_format(conf);
    } else {
        ESP_LOGI(TAG, "Partition size: total: %d, used: %d", total, used);
    }
}

/**
 * @brief Check if a file exists in SPIFFS, by path
 * 
 * @param path path to file to check
 * @return true if the file exists
 * @return false if the file does not exist
 */
bool spiffs_file_exists(const char *path)
{
    struct stat st;
    if (stat(path, &st) == 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * @brief Write a file to SPIFFS, by path
 * 
 * @param path path to file to write
 * @param message message to write to file
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_write_file(const char *path, const char *message)
{
    FILE* f;
    if (!spiffs_file_exists(path)) {
        f = fopen(path, "w");
    } else {
        f = fopen(path, "a");
    }

    if (f == NULL) {
        ESP_LOGE(TAG, "Failed to open file for writing");
        return ESP_FAIL;
    }
    fprintf(f, "%s\n", message);
    fclose(f);
    ESP_LOGI(TAG, "File written");

    return ESP_OK;
}

/**
 * @brief Read a file from SPIFFS, by path
 * 
 * @param path path to file to read
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_read_file(const char *path)
{
    ESP_LOGI(TAG, "Reading file: %s", path);
    if (!spiffs_file_exists(path)) {
        ESP_LOGE(TAG, "File does not exist!");
        return ESP_FAIL;
    } else {
        FILE *file = fopen(path, "r");
        char line[256];
        while (fgets(line, sizeof(line), file) != NULL) {
            printf(line);
        }
        fclose(file);
    }

    return ESP_OK;
}

/**
 * @brief Delete a file from SPIFFS, by path
 * 
 * @param path path to file to delete
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_delete_file(const char *path)
{
    if (unlink(path) != 0) {
        ESP_LOGE(TAG, "Failed to delete file");
        return ESP_FAIL;
    }

    return ESP_OK;
}

/**
 * @brief Unmount SPIFFS partition
 * 
 * @param conf configuration struct for SPIFFS
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t spiffs_unmount(esp_vfs_spiffs_conf_t *conf)
{
    return esp_vfs_spiffs_unregister(conf->partition_label);
}
