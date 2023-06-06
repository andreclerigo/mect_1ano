#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "driver/i2c.h"
#include "ssd1306.h"
#include "font8x8_basic.h"

#define I2C_NUM I2C_NUM_0
#define I2C_MASTER_FREQ_HZ 400000


/**
 * @brief Configure I2C parameters and commands for SSD1306
 * 
 * @param dev device handle for SSD1306
 * @param width the width of the display
 * @param height the height of the display
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it faileds
 */
static esp_err_t _i2c_init(SSD1306_t *dev, int width, int height)
{
	// Setting the caracheristics of the display, could be static tho
	dev->_width = width;
	dev->_height = height;
	dev->_lines = 4;
	
	// BELLOW LIES BLACK MAGIC, DO NOT TOUCH
	i2c_cmd_handle_t cmd = i2c_cmd_link_create();
	i2c_master_start(cmd);
	i2c_master_write_byte(cmd, (dev->_address << 1) | I2C_MASTER_WRITE, true);
	i2c_master_write_byte(cmd, OLED_CONTROL_BYTE_CMD_STREAM, true);
	i2c_master_write_byte(cmd, OLED_CMD_DISPLAY_OFF, true);				// AE
	i2c_master_write_byte(cmd, OLED_CMD_SET_MUX_RATIO, true);			// A8
	i2c_master_write_byte(cmd, 0x1F, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_DISPLAY_OFFSET, true);		// D3
	i2c_master_write_byte(cmd, 0x00, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_DISPLAY_START_LINE, true);	// 40
	i2c_master_write_byte(cmd, OLED_CMD_SET_SEGMENT_REMAP_1, true);		// A1
	i2c_master_write_byte(cmd, OLED_CMD_SET_COM_SCAN_MODE, true);		// C8
	i2c_master_write_byte(cmd, OLED_CMD_SET_DISPLAY_CLK_DIV, true);		// D5
	i2c_master_write_byte(cmd, 0x80, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_COM_PIN_MAP, true);			// DA
	i2c_master_write_byte(cmd, 0x02, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_CONTRAST, true);			// 81
	i2c_master_write_byte(cmd, 0xFF, true);
	i2c_master_write_byte(cmd, OLED_CMD_DISPLAY_RAM, true);				// A4
	i2c_master_write_byte(cmd, OLED_CMD_SET_VCOMH_DESELCT, true);		// DB
	i2c_master_write_byte(cmd, 0x40, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_MEMORY_ADDR_MODE, true);	// 20
	i2c_master_write_byte(cmd, OLED_CMD_SET_PAGE_ADDR_MODE, true);		// 02
	// Set Lower Column Start Address for line Addressing Mode
	i2c_master_write_byte(cmd, 0x00, true);
	// Set Higher Column Start Address for line Addressing Mode
	i2c_master_write_byte(cmd, 0x10, true);
	i2c_master_write_byte(cmd, OLED_CMD_SET_CHARGE_PUMP, true);			// 8D
	i2c_master_write_byte(cmd, 0x14, true);
	i2c_master_write_byte(cmd, OLED_CMD_DEACTIVE_SCROLL, true);			// 2E
	i2c_master_write_byte(cmd, OLED_CMD_DISPLAY_NORMAL, true);			// A6
	i2c_master_write_byte(cmd, OLED_CMD_DISPLAY_ON, true);				// AF

	i2c_master_stop(cmd);
	esp_err_t ret = i2c_master_cmd_begin(I2C_NUM, cmd, 10/portTICK_PERIOD_MS);
	i2c_cmd_link_delete(cmd);

	return ret;
}

/**
 * @brief Display a character on the screen.
 * 
 * @param dev device handle for SSD1306
 * @param line the line to display the character on
 * @param pixel the pixel to display the character on
 * @param characters the characters to display
 * @param width the width of the display
 */
static void _i2c_display_character(SSD1306_t *dev, int line, int pixel, uint8_t *characters, int width)
{
	i2c_cmd_handle_t cmd;

	if (line >= dev->_lines) return;
	if (pixel >= dev->_width) return;

	// int _pixel = pixel + CONFIG_OFFSETX;
	int _pixel = pixel + 0;
	uint8_t columLow = _pixel & 0x0F;
	uint8_t columHigh = (_pixel >> 4) & 0x0F;

	int _line = line;

	cmd = i2c_cmd_link_create();
	i2c_master_start(cmd);
	i2c_master_write_byte(cmd, (dev->_address << 1) | I2C_MASTER_WRITE, true);

	i2c_master_write_byte(cmd, OLED_CONTROL_BYTE_CMD_STREAM, true);
	// Set Lower Column Start Address for line Addressing Mode
	i2c_master_write_byte(cmd, (0x00 + columLow), true);
	// Set Higher Column Start Address for line Addressing Mode
	i2c_master_write_byte(cmd, (0x10 + columHigh), true);
	// Set line Start Address for line Addressing Mode
	i2c_master_write_byte(cmd, 0xB0 | _line, true);

	i2c_master_stop(cmd);
	i2c_master_cmd_begin(I2C_NUM, cmd, 10/portTICK_PERIOD_MS);
	i2c_cmd_link_delete(cmd);

	cmd = i2c_cmd_link_create();
	i2c_master_start(cmd);
	i2c_master_write_byte(cmd, (dev->_address << 1) | I2C_MASTER_WRITE, true);

	i2c_master_write_byte(cmd, OLED_CONTROL_BYTE_DATA_STREAM, true);
	i2c_master_write(cmd, characters, width, true);

	i2c_master_stop(cmd);
	i2c_master_cmd_begin(I2C_NUM, cmd, 10/portTICK_PERIOD_MS);
	i2c_cmd_link_delete(cmd);
}

/**
 * @brief Display an character on the screen.
 * 
 * @param dev device handle for SSD1306
 * @param line the line to display the character on
 * @param pixel the pixel to display the character on
 * @param characters the characters to display
 * @param width the width of the display
 */
static void _ssd1306_display_character(SSD1306_t *dev, int line, int pixel, uint8_t *characters, int width)
{
	_i2c_display_character(dev, line, pixel, characters, width);
	
	// Set to internal buffer
	memcpy(&dev->_line[line]._pixels[pixel], characters, width);
}

/**
 * @brief Initialize the SSD1306
 * 
 * @param dev device handle for SSD1306
 * @param sda the pin number for the SDA signal
 * @param scl the pin number for the SCL signal
 * @param width the width of the display
 * @param height the height of the display
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it faileds
 */
esp_err_t ssd1306_init(SSD1306_t *dev, int16_t sda, int16_t scl, int width, int height)
{
	i2c_config_t i2c_config = {
		.mode = I2C_MODE_MASTER,
		.sda_io_num = sda,
		.scl_io_num = scl,
		.sda_pullup_en = GPIO_PULLUP_ENABLE,
		.scl_pullup_en = GPIO_PULLUP_ENABLE,
		.master.clk_speed = I2C_MASTER_FREQ_HZ
	};
	ESP_ERROR_CHECK(i2c_param_config(I2C_NUM, &i2c_config));
	ESP_ERROR_CHECK(i2c_driver_install(I2C_NUM, I2C_MODE_MASTER, 0, 0, 0));
	dev->_address = I2CAddress;

	esp_err_t ret = _i2c_init(dev, width, height);
	
	// Initialize internal buffer
	for (int i = 0; i < dev->_lines; i++)
		memset(dev->_line[i]._pixels, 0, 128);

	return ret;
}

/**
 * @brief Free the SSD1306
 * 
 * @param dev device handle for SSD1306
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t ssd1306_free(SSD1306_t *dev)
{
	esp_err_t ret = i2c_driver_delete(I2C_NUM);
	if (ret != ESP_OK) {
		return ret;
	}
	return ESP_OK;
}

/**
 * @brief Display a string on the screen.
 * 
 * @param dev device handle for SSD1306
 * @param line the line to display the string on
 * @param text the string to display
 * @param text_len the length of the string
 */
void ssd1306_display_text(SSD1306_t *dev, int line, char *text, int text_len)
{
	// This should generate some errors but then I would have to check the return value everytime
	// Urghhh....
	if (line >= dev->_lines) return;
	int _text_len = text_len;
	if (_text_len > 16) _text_len = 16;

	uint8_t pixel = 0;
	uint8_t character[8];
	// Display each character
	for (uint8_t i = 0; i < _text_len; i++) {
		memcpy(character, font8x8_basic_tr[(uint8_t) text[i]], 8);
		_ssd1306_display_character(dev, line, pixel, character, 8);
		pixel = pixel + 8;
	}
}

/**
 * @brief Clear the screen which clears all the lines.
 * 
 * @param dev device handle for SSD1306
 */
void ssd1306_clear_screen(SSD1306_t *dev)
{
	for (int line = 0; line < dev->_lines; line++)
		ssd1306_clear_line(dev, line);
}

/**
 * @brief Clear a line on the screen.
 * 
 * @param dev device handle for SSD1306
 * @param line the line to clear
 */
void ssd1306_clear_line(SSD1306_t *dev, int line)
{
	char characters[16];
	memset(characters, 0x00, sizeof(characters));
	ssd1306_display_text(dev, line, characters, sizeof(characters));
}
