#include "temp_sensor_TC74.h"
#include "esp_log.h"

#define TC74_READ_COMMAND           0x00            /* Command read for TC74*/ 
#define TC74_CONFIG_REGISTER        0x01
#define TC74_CONFIG_STANDBY         0x01
#define TC74_CONFIG_WAKEUP          0x00
#define I2C_MASTER_TX_BUF_DISABLE   0               /* I2C master doesn't need buffer */
#define I2C_MASTER_RX_BUF_DISABLE   0               /* I2C master doesn't need buffer */

static const char *TAG = "TC74";

esp_err_t tc74_init(i2c_port_t i2cPort, int sdaPin, int sclPin, uint32_t clkSpeedHz)
{
    int i2c_master_port = i2cPort;

    i2c_config_t conf = {
        .mode = I2C_MODE_MASTER,
        .sda_io_num = sdaPin,
        .scl_io_num = sclPin,
        .sda_pullup_en = GPIO_PULLUP_ENABLE,
        .scl_pullup_en = GPIO_PULLUP_ENABLE,
        .master.clk_speed = clkSpeedHz,
    };

    i2c_param_config(i2c_master_port, &conf);

    return i2c_driver_install(i2c_master_port, conf.mode, I2C_MASTER_RX_BUF_DISABLE, I2C_MASTER_TX_BUF_DISABLE, 0);
}

esp_err_t tc_74_free(i2c_port_t i2cPort)
{
    i2c_driver_delete(i2cPort);
    return ESP_OK;
}

esp_err_t tc74_standby(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut)
{
    uint8_t data[2] = {TC74_CONFIG_REGISTER, TC74_CONFIG_STANDBY};
    return i2c_master_write_to_device(i2cPort, sensAddr, data, 2, timeOut / portTICK_PERIOD_MS);
}

esp_err_t tc74_wakeup(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut)
{
    uint8_t data[2] = {TC74_CONFIG_REGISTER, TC74_CONFIG_WAKEUP};
    return i2c_master_write_to_device(i2cPort, sensAddr, data, 2, timeOut / portTICK_PERIOD_MS);
}

bool tc74_is_temperature_ready(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut)
{
    uint8_t config_reg = 0;
    uint8_t config_register = TC74_CONFIG_REGISTER;
    esp_err_t err = i2c_master_write_read_device(i2cPort, sensAddr, &config_register, 1, &config_reg, 1, timeOut / portTICK_PERIOD_MS);
    if (err == ESP_OK && (config_reg & 0x40) != 0) {
        return true;
    }

    return false;
}

esp_err_t tc74_wakeup_and_read_temp(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut, uint8_t* pData)
{
    esp_err_t err = tc74_wakeup(i2cPort, sensAddr, timeOut);
    if (err != ESP_OK) return err;

    while (!tc74_is_temperature_ready(i2cPort, sensAddr, timeOut)) {
        vTaskDelay(10 / portTICK_PERIOD_MS);
    }

    return tc74_read_temp_after_cfg(i2cPort, sensAddr, timeOut, pData);
}

esp_err_t tc74_read_temp_after_cfg(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut, uint8_t* pData)
{
    uint8_t read_command = TC74_READ_COMMAND;
    return i2c_master_write_read_device(i2cPort, sensAddr, &read_command, 1, pData, 1, timeOut / portTICK_PERIOD_MS);
}

esp_err_t tc74_read_temp_after_temp(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut, uint8_t* pData)
{
    return i2c_master_read_from_device(i2cPort, sensAddr, pData, 1, timeOut / portTICK_PERIOD_MS);
}
