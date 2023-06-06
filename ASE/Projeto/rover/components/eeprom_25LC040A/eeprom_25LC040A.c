#include "eeprom_25LC040A.h"
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/spi_master.h"

// SPI commands for the 25LC040A EEPROM
#define WREN   0x06
#define WRDI   0x04
#define RDSR   0x05
#define WRSR   0x01
#define READ   0x03
#define WRITE  0x02


/**
 * @brief Initialize the SPI bus and device for the 25LC040A EEPROM.
 * 
 * @param masterHostId the device id for the SPI host
 * @param csPin the pin number for the CS signal
 * @param sckPin the pin number for the SCK signal
 * @param mosiPin the pin number for the MOSI signal
 * @param misoPin the pin number for the MISO signal
 * @param clkSpeedHz the clock speed in Hz
 * @param pDevHandle pointer to the device handle
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t eeprom_init(spi_host_device_t masterHostId, int csPin, int sckPin, int mosiPin, int misoPin, 
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

/**
 * @brief Operation to free the SPI bus.
 * 
 * @param masterHostId the device id for the SPI host
 * @param devHandle device handler used for the spi driver 
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t eeprom_free(spi_host_device_t masterHostId, spi_device_handle_t devHandle)
{
    esp_err_t ret = spi_bus_remove_device(devHandle);
    if (ret == ESP_OK) {
        ret = spi_bus_free(masterHostId);
    }
    return ret;
}

/**
 * @brief Transmit data over SPI bus.
 * 
 * @param spiHandle The SPI device handle
 * @param pBuffer The buffer containing the data to be transmitted
 * @param bufSize The size of the buffer in bytes
 */
static void _spi_tx_data(spi_device_handle_t spiHandle, const uint8_t* pBuffer, int bufSize)
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

/**
 * @brief Transmit and receive data over SPI bus.
 * 
 * @param spiHandle The SPI device handle
 * @param pTxBuffer The buffer containing the data to be transmitted
 * @param txBufSize The size of the transmit buffer in bytes
 * @param pRxBuffer The buffer to store the received data
 * @param rxBufSize The size of the receive buffer in bytes
 */
static void _spi_tx_rx_data(spi_device_handle_t spiHandle, const uint8_t* pTxBuffer, int txBufSize,
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

/**
 * @brief Read a byte from a specific address of the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @param address The address to read from
 * @param pData Pointer to a buffer to store the read data
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_read_byte(spi_device_handle_t devHandle, uint16_t address, uint8_t* pData)
{
    uint8_t inst;
    uint16_t addr_bit8;
    addr_bit8 = address & 0x0100;
    inst = READ | (addr_bit8 >> 5);
    uint8_t txData[2] = {inst, address}; //0x03 to use the read_command, then splitting the address into a low and a high byte as the spi protocol demands.

    _spi_tx_rx_data(devHandle, txData, sizeof(txData), pData, 1);
    return ESP_OK;
}

/**
 * @brief Write a byte to a specific address of the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @param address The address to write to
 * @param data The data to be written
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_write_byte(spi_device_handle_t devHandle, uint16_t address, uint8_t data)
{
    eeprom_write_enable(devHandle);
    vTaskDelay(10);

    uint8_t inst;
    uint16_t addr_bit8;
    addr_bit8 = address & 0x0100;
    inst = WRITE | (addr_bit8 >> 5);
    uint8_t txData[3] = {inst, address, data}; //0x0 to use the write_command, then splitting the address into a low and a high byte as the spi protocol demands.
    
    _spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

/**
 * @brief Write a page (16 bytes) to a specific address of the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @param address The starting address to write to
 * @param pBuffer The buffer containing the data to be written
 * @param size The size of the buffer in bytes (must not exceed 16)
 * @return esp_err_t ESP_OK if successful, ESP_ERR_INVALID_SIZE if size > 16, ESP_FAIL otherwise
 */
esp_err_t eeprom_write_page(spi_device_handle_t devHandle, uint16_t address, const uint8_t* pBuffer, uint8_t size)
{
    if (size > 16) {
        return ESP_ERR_INVALID_SIZE;
    }

    eeprom_write_enable(devHandle);
    vTaskDelay(10);

    esp_err_t ret;
    uint8_t inst;
    uint16_t addr_bit8;
    addr_bit8 = address & 0x0100;
    inst = 0x02 | (addr_bit8 >> 5);
    uint8_t write_page_seq[18] = {inst, address, pBuffer[0], pBuffer[1], pBuffer[2], pBuffer[3], pBuffer[4], pBuffer[5], pBuffer[6],
                                  pBuffer[7], pBuffer[8], pBuffer[9], pBuffer[10], pBuffer[11], pBuffer[12], pBuffer[13], pBuffer[14],
                                  pBuffer[15]}; //0x0 to use the write_command, then splitting the address into a low and a high byte as the spi protocol demands.
    spi_transaction_t spiTrans;

    memset(&spiTrans, 0, sizeof(spiTrans));
    spiTrans.length = 8 * (2 + size);
    spiTrans.tx_buffer = write_page_seq; 

    ret = spi_device_polling_transmit(devHandle, &spiTrans);
    assert(ret == ESP_OK);

    return ret;
}

/**
 * @brief Enable write operations on the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_write_enable(spi_device_handle_t devHandle)
{
    uint8_t txData[1] = {WREN};

    _spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

/**
 * @brief Disable write operations on the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_write_disable(spi_device_handle_t devHandle)
{
    uint8_t txData[1] = {WRDI};

    _spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}

/**
 * @brief Read the status register of the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @param pStatus Pointer to a buffer to store the status register value
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_read_status(spi_device_handle_t devHandle, uint8_t* pStatus)
{
    uint8_t txData[1] = {RDSR};

    _spi_tx_rx_data(devHandle, txData, sizeof(txData), pStatus, 1);
    return ESP_OK;
}

/**
 * @brief Write to the status register of the 25LC040A EEPROM.
 * 
 * @param devHandle The SPI device handle
 * @param status The status register value to be written
 * @return esp_err_t ESP_OK if successful, ESP_FAIL otherwise
 */
esp_err_t eeprom_write_status(spi_device_handle_t devHandle, uint8_t status)
{
    eeprom_write_enable(devHandle);
    vTaskDelay(10);
    
    uint8_t txData[2] = {WRSR, status};
    
    _spi_tx_data(devHandle, txData, sizeof(txData));
    return ESP_OK;
}
