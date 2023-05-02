#include <stdio.h>
#include "esp_log.h"
#include "driver/i2c.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

static const char *TAG = "i2c-dummy";

#define I2C_MASTER_SCL_IO           15              /* GPIO number used for I2C master clock */
#define I2C_MASTER_SDA_IO           13              /* GPIO number used for I2C master data  */
// #define I2C_MASTER_SDA_IO           2               /* GPIO number used for I2C master data  */
#define I2C_MASTER_NUM              0               /* I2C master i2c port number, the number of i2c peripheral interfaces available will depend on the chip */
#define I2C_MASTER_FREQ_HZ          50000           /* I2C master clock frequency */
#define I2C_MASTER_TX_BUF_DISABLE   0               /* I2C master doesn't need buffer */
#define I2C_MASTER_RX_BUF_DISABLE   0               /* I2C master doesn't need buffer */
#define I2C_MASTER_TIMEOUT_MS       1000            /* I2C master timeout 1s */

#define TC74_SENSOR_ADDR            0x4D            /* Slave address of the TC74 sensor */
#define TC74_READ_COMMAND           0x00            /* Command read for TC74*/


static esp_err_t tc74_register_read(uint8_t reg_addr, uint8_t *data, size_t len)
{
    return i2c_master_write_read_device(I2C_MASTER_NUM, TC74_SENSOR_ADDR, &reg_addr, 1, data, len, I2C_MASTER_TIMEOUT_MS / portTICK_PERIOD_MS);
}

static esp_err_t tc74_register_write_byte(uint8_t *write_data)
{
    int ret;

    ret = i2c_master_write_to_device(I2C_MASTER_NUM, TC74_SENSOR_ADDR, &write_data, 1, I2C_MASTER_TIMEOUT_MS / portTICK_PERIOD_MS);

    return ret;
}

static esp_err_t i2c_master_init(void)
{
    int i2c_master_port = I2C_MASTER_NUM;

    i2c_config_t conf = {
        .mode = I2C_MODE_MASTER,
        .sda_io_num = I2C_MASTER_SDA_IO,
        .scl_io_num = I2C_MASTER_SCL_IO,
        .sda_pullup_en = GPIO_PULLUP_ENABLE,
        .scl_pullup_en = GPIO_PULLUP_ENABLE,
        .master.clk_speed = I2C_MASTER_FREQ_HZ,
    };

    i2c_param_config(i2c_master_port, &conf);

    return i2c_driver_install(i2c_master_port, conf.mode, I2C_MASTER_RX_BUF_DISABLE, I2C_MASTER_TX_BUF_DISABLE, 0);
}


void app_main(void)
{
    uint8_t read_data[1];
    uint8_t write_data = 0x5A;
    ESP_ERROR_CHECK(i2c_master_init());
    ESP_LOGI(TAG, "I2C initialized successfully");

    while (1) {
        // Write data to TC74
        // ESP_ERROR_CHECK(tc74_register_write_byte(&write_data));
        // vTaskDelay(pdMS_TO_TICKS(1000));

        // Read data from TC74
        tc74_register_read(TC74_READ_COMMAND, read_data, 1);
        ESP_LOGI(TAG, "TEMP0 = %d", read_data[0]);
        vTaskDelay(pdMS_TO_TICKS(1000));
    }
}
