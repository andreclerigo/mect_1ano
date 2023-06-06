#ifndef SSD1306_H_
#define SSD1306_H_

// This code is based on the following source (the datasheet is not very helpful)):
// http://robotcantalk.blogspot.com/2015/03/interfacing-arduino-with-ssd1306-driven.html

/* 
Control byte for i2c
Co : bit 8 : Continuation Bit 
	* 1 = no-continuation (only one byte to follow) 
	* 0 = the controller should expect a stream of bytes. 
D/C# : bit 7 : Data/Command Select bit 
	* 1 = the next byte or byte stream will be Data. 
	* 0 = a Command byte or byte stream will be coming up next. 
	Bits 6-0 will be all zeros. 
Usage: 
	0x80 : Single Command byte 
	0x00 : Command Stream 
	0xC0 : Single Data byte 
	0x40 : Data Stream
*/

#define OLED_CONTROL_BYTE_CMD_STREAM    0x00
#define OLED_CONTROL_BYTE_DATA_STREAM   0x40

// Fundamental commands (pg.28)
#define OLED_CMD_SET_CONTRAST           0x81    // follow with 0x7F
#define OLED_CMD_DISPLAY_RAM            0xA4
#define OLED_CMD_DISPLAY_NORMAL         0xA6
#define OLED_CMD_DISPLAY_INVERTED       0xA7
#define OLED_CMD_DISPLAY_OFF            0xAE
#define OLED_CMD_DISPLAY_ON             0xAF

// Addressing Command Table (pg.30)
#define OLED_CMD_SET_MEMORY_ADDR_MODE   0x20
#define OLED_CMD_SET_PAGE_ADDR_MODE     0x02    // Page Addressing Mode

// Hardware Config (pg.31)
#define OLED_CMD_SET_DISPLAY_START_LINE 0x40
#define OLED_CMD_SET_SEGMENT_REMAP_1    0xA1    
#define OLED_CMD_SET_MUX_RATIO          0xA8    // follow with 0x3F = 64 MUX
#define OLED_CMD_SET_COM_SCAN_MODE      0xC8    
#define OLED_CMD_SET_DISPLAY_OFFSET     0xD3    // follow with 0x00
#define OLED_CMD_SET_COM_PIN_MAP        0xDA    // follow with 0x12

// Timing and Driving Scheme (pg.32)
#define OLED_CMD_SET_DISPLAY_CLK_DIV    0xD5    // follow with 0x80
#define OLED_CMD_SET_VCOMH_DESELCT      0xDB    // follow with 0x30

// Charge Pump (pg.62)
#define OLED_CMD_SET_CHARGE_PUMP        0x8D    // follow with 0x14

// Scrolling Command
#define OLED_CMD_DEACTIVE_SCROLL        0x2E

#define I2CAddress 0x3C

typedef struct {
	uint8_t _pixels[128];	// Pixels
} PAGE_t;

typedef struct {
	int _address;
	int _width;
	int _height;
	int _lines;				// Number of lines
	PAGE_t _line[8];
} SSD1306_t;

esp_err_t ssd1306_init(SSD1306_t *dev, int16_t sda, int16_t scl, int width, int height);
esp_err_t ssd1306_free(SSD1306_t *dev);
void ssd1306_display_text(SSD1306_t *dev, int page, char *text, int text_len);
void ssd1306_clear_screen(SSD1306_t *dev);
void ssd1306_clear_line(SSD1306_t *dev, int page);

#endif // SSD1306_H_
