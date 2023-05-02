#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/spi_master.h"

#define SPI_MASTER_HOST SPI3_HOST

#define PIN_SPI_SS      5
#define PIN_SPI_SCLK    18
#define PIN_SPI_MOSI    23
#define PIN_SPI_MISO    19

void spi_tx_data(spi_device_handle_t spiHandle, const uint8_t* pBuffer, int bufSize)
{
    esp_err_t ret;
    spi_transaction_t spiTrans;

    if (bufSize <= 0)
    {
        return;
    }
    memset(&spiTrans, 0, sizeof(spiTrans));
    spiTrans.length = bufSize * 8;
    spiTrans.tx_buffer = pBuffer;
    ret = spi_device_polling_transmit(spiHandle, &spiTrans);
    assert (ret==ESP_OK);
}

void spi_tx_rx_data(spi_device_handle_t spiHandle, const uint8_t* pTxBuffer, int txBufSize,
                                                   uint8_t* pRxBuffer, int rxBufSize)
{
    esp_err_t ret;
    spi_transaction_t spiTrans;

    if (txBufSize <= 0)
    {
        return;
    }
    memset(&spiTrans, 0, sizeof(spiTrans));
    spiTrans.length = (txBufSize + rxBufSize) * 8;
    spiTrans.tx_buffer = pTxBuffer;
    spiTrans.rxlength = rxBufSize * 8;
    spiTrans.rx_buffer = pRxBuffer;
    ret = spi_device_polling_transmit(spiHandle, &spiTrans);
    assert (ret==ESP_OK);
}

void app_main(void)
{
    esp_err_t ret;
    spi_device_handle_t spiHandle;

    spi_bus_config_t spiBusCfg = {
        .mosi_io_num = PIN_SPI_MOSI,
        .miso_io_num = PIN_SPI_MISO,
        .sclk_io_num = PIN_SPI_SCLK,
        .quadwp_io_num = -1,
        .quadhd_io_num = -1,
        .max_transfer_sz = 32,
        .flags = SPICOMMON_BUSFLAG_MASTER
    };

    spi_device_interface_config_t spiDeviceCfg = {
        .command_bits = 0,
        .address_bits = 0,
        .dummy_bits = 0,
        .mode = 0,
        .clock_speed_hz = 1000000,
        .spics_io_num = PIN_SPI_SS,
        .queue_size = 1
    };

    ret = spi_bus_initialize(SPI_MASTER_HOST, &spiBusCfg, SPI_DMA_DISABLED);
    ESP_ERROR_CHECK(ret);

    ret=spi_bus_add_device(SPI_MASTER_HOST, &spiDeviceCfg, &spiHandle);
    ESP_ERROR_CHECK(ret);
    
    uint8_t txData1[2] = {0x5A, 0xC3}; // {0x01, 0x0C};

    //spi_tx_data(spiHandle, txData1, sizeof(txData1));

    uint8_t txData2 = 0x05;
    uint8_t rxData;

    while (1)
    {
        spi_tx_data(spiHandle, txData1, sizeof(txData1));
        //spi_tx_rx_data(spiHandle, &txData2, sizeof(txData2), &rxData, sizeof(rxData));
        vTaskDelay(100 / portTICK_PERIOD_MS);
    }
}
