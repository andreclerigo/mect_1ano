#ifndef SPIFFS_H_
#define SPIFFS_H_

#include "esp_err.h"
#include "esp_log.h"
#include "esp_spiffs.h"
#include <stdio.h>
#include <string.h>
#include <sys/unistd.h>
#include <sys/stat.h>

esp_err_t spiffs_mount(esp_vfs_spiffs_conf_t *conf);
esp_err_t spiffs_check(esp_vfs_spiffs_conf_t *conf);
void spiffs_get_info(esp_vfs_spiffs_conf_t *conf);
esp_err_t spiffs_format(esp_vfs_spiffs_conf_t *conf);
esp_err_t spiffs_write_file(const char *path, const char *message);
esp_err_t spiffs_read_file(const char *path);
bool spiffs_file_exists(const char *path);
esp_err_t spiffs_delete_file(const char *path);
esp_err_t spiffs_unmount(esp_vfs_spiffs_conf_t *conf);

#endif // SPIFFS_H_
