#include "spi_25LC040A_eeprom.h"
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

// SPI commands for the 25LC040A EEPROM
#define WREN   0x06
#define WRDI   0x04
#define RDSR   0x05
#define WRSR   0x01
#define READ   0x03
#define WRITE  0x02

static const char *TAG = "25LC040A_eeprom";


esp_err_t spi_25LC040_init(spi_host_device_t masterHostId, int csPin, int sckPin, int mosiPin, int misoPin, 
                           int clkSpeedHz, spi_device_handle_t* pDevHandle)
{
    esp_err_t ret;

    spi_bus_config_t spiBusCfg = {
        .miso_io_num = misoPin,
        .mosi_io_num = mosiPin,
        .sclk_io_num = sckPin,
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
        .clock_speed_hz = clkSpeedHz,
        .spics_io_num = csPin,
        .flags = SPI_DEVICE_HALFDUPLEX,
        .queue_size = 1
    };

    ret = spi_bus_initialize(masterHostId, &spiBusCfg, SPI_DMA_DISABLED);
    if (ret != ESP_OK) {
        return ret;
    }

    ret = spi_bus_add_device(masterHostId, &spiDeviceCfg, pDevHandle);
    if (ret != ESP_OK) {
        spi_bus_free(masterHostId);
        return ret;
    }

    return ESP_OK;
}

esp_err_t spi_25LC040_free(spi_host_device_t masterHostId, spi_device_handle_t devHandle)
{
    esp_err_t ret = spi_bus_remove_device(devHandle);
    if (ret == ESP_OK) {
        ret = spi_bus_free(masterHostId);
    }
    return ret;
}

static void spi_tx_data(spi_device_handle_t spiHandle, const uint8_t* pBuffer, int bufSize)
{
    esp_err_t ret;
    spi_transaction_t spiTrans;

    if (bufSize <= 0) {
        return;
    }
    memset(&spiTrans, 0, sizeof(spiTrans));
    spiTrans.length = bufSize * 8;
    spiTrans.tx_buffer = pBuffer;
    ret = spi_device_polling_transmit(spiHandle, &spiTrans);
    assert (ret==ESP_OK);
}

static void spi_tx_rx_data(spi_device_handle_t spiHandle, const uint8_t* pTxBuffer, int txBufSize,
                                                          uint8_t* pRxBuffer, int rxBufSize)
{
    esp_err_t ret;
    spi_transaction_t spiTrans;

    if (txBufSize <= 0) {
        return;
    }
    memset(&spiTrans, 0, sizeof(spiTrans));
    spiTrans.length = txBufSize * 8;
    spiTrans.tx_buffer = pTxBuffer;
    spiTrans.rxlength = rxBufSize * 8;
    spiTrans.rx_buffer = pRxBuffer;
    ret = spi_device_polling_transmit(spiHandle, &spiTrans);
    assert (ret==ESP_OK);
}

esp_err_t spi_25LC040_read_byte(spi_device_handle_t devHandle, uint16_t address, uint8_t* pData)
{
    uint8_t inst;
    uint16_t addr_bit8;
    addr_bit8 = address & 0x0100;
    inst = READ | (addr_bit8 >> 5);
    uint8_t txData[2] = {inst, address}; //0x03 to use the read_command, then splitting the address into a low and a high byte as the spi protocol demands.

    spi_tx_rx_data(devHandle, txData, sizeof(txData), pData, 1);
    return ESP_OK;
}

esp_err_t spi_25LC040_write_byte(spi_device_handle_t devHandle, uint16_t address, uint8_t data)
{
    spi_25LC040_write_enable(devHandle);
    vTaskDelay(10);

    uint8_t inst;
    uint16_t addr_bit8;
    addr_bit8 = address & 0x0100;
    inst = WRITE | (addr_bit8 >> 5);
    uint8_t txData[3] = {inst, address, data}; //0x0 to use the write_command, then splitting the address into a low and a high byte as the spi protocol demands.
    
    spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

esp_err_t spi_25LC040_write_page(spi_device_handle_t devHandle, uint16_t address, const uint8_t* pBuffer, uint8_t size)
{
    if (size > 16) {
        return ESP_ERR_INVALID_SIZE;
    }

    for (uint8_t i  = 0; i < size; i++) {
        spi_25LC040_write_byte(devHandle, address, pBuffer[i]);
        vTaskDelay(pdMS_TO_TICKS(10));
        address++;
    }

    return ESP_OK;
}

esp_err_t spi_25LC040_write_enable(spi_device_handle_t devHandle)
{
    uint8_t txData[1] = {WREN};

    spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

esp_err_t spi_25LC040_write_disable(spi_device_handle_t devHandle)
{
    uint8_t txData[1] = {WRDI};

    spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

esp_err_t spi_25LC040_read_status(spi_device_handle_t devHandle, uint8_t* pStatus)
{
    uint8_t txData[1] = {RDSR};

    spi_tx_rx_data(devHandle, txData, sizeof(txData), pStatus, 1);
    return ESP_OK;
}

esp_err_t spi_25LC040_write_status(spi_device_handle_t devHandle, uint8_t status)
{    
    uint8_t txData[2] = {WRSR, status};
    
    spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}
