#pragma once
#include "driver/i2c.h"

esp_err_t tc74_init(i2c_port_t i2cPort, int sdaPin, int sclPin, uint32_t clkSpeedHz);

esp_err_t tc_74_free(i2c_port_t i2cPort);

esp_err_t tc74_standby(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut);

esp_err_t tc74_wakeup(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut);

bool tc74_is_temperature_ready(i2c_port_t i2cPort, uint8_t sensAddr, TickType_t timeOut);

esp_err_t tc74_wakeup_and_read_temp(i2c_port_t i2cPort, uint8_t sensAddr,
                                    TickType_t timeOut, uint8_t* pData);

esp_err_t tc74_read_temp_after_cfg(i2c_port_t i2cPort, uint8_t sensAddr,
                                   TickType_t timeOut, uint8_t* pData);

esp_err_t tc74_read_temp_after_temp(i2c_port_t i2cPort, uint8_t sensAddr,
                                    TickType_t timeOut, uint8_t* pData);
