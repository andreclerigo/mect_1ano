#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include "time.h"
#include "nvs_flash.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"
#include "esp_wifi.h"
#include "esp_timer.h"
#include "esp_event.h"
#include "esp_netif.h"
#include "esp_sntp.h"
#include "esp_http_server.h"
#include "esp_http_client.h"
#include "cJSON.h"
#include "dht11.h"
#include "mq5.h"
#include "eeprom_25LC040A.h"
#include "ssd1306.h"
#include "buzzer.h"
#include "spiffs.h"

// Wi-Fi Configuration
#define WIFI_SSID           CONFIG_WIFI_SSID
#define WIFI_PASS           CONFIG_WIFI_PASS
#define SERVER_IP_ADDR      CONFIG_SERVER_IP_ADDR
// DHT11 Configuration
#define DHT11_PIN           CONFIG_DHT11_PIN
// Buzzer Configuration
#define BUZZER_PIN          CONFIG_BUZZER_PIN
// EEPROM Configuration
#define CS_PIN              CONFIG_EEPROM_CS_PIN
#define SCK_PIN             CONFIG_EEPROM_SCK_PIN
#define MOSI_PIN            CONFIG_EEPROM_MOSI_PIN
#define MISO_PIN            CONFIG_EEPROM_MISO_PIN
#define CLK_SPEED_HZ        CONFIG_EEPROM_CLK_SPEED_HZ
// SSD1306 Configuration
#define SCL_PIN             CONFIG_SSD1306_SCL_PIN
#define SDA_PIN             CONFIG_SSD1306_SDA_PIN
// MQ-5 Configuration
#define MQ5_PIN             CONFIG_MQ5_PIN
#define A                   76.9
#define B                   -2.49
#define RL                  5500.0
// SPIFFS Configuration
#define SPIFFS_TEMP_PATH    "/spiffs/temp.txt"
#define SPIFFS_HUMID_PATH   "/spiffs/humid.txt"
#define SPIFFS_GAS_PATH     "/spiffs/gas.txt"

// Function prototypes
static void setup_pins(void);
static void stop(void);
static void forward(void);
static void backward(void);
static void right(void);
static void left(void);

// Event handlers
static void on_got_ip(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data);
static void on_wifi_disconnect(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data);

// Initialization functions
static void wifi_init(void);
static void http_server_init(void);

// HTTP request handler
static esp_err_t control_handler(httpd_req_t *req);
static esp_err_t ping_handler(httpd_req_t *req);

// SNTP functions
void obtain_time(void);

// Constants and relay pin numbers
static const char *TAG = "ROVER";
static uint8_t RELAY_1 = 25;
static uint8_t RELAY_2 = 26;
static uint8_t RELAY_3 = 27;
static uint8_t RELAY_4 = 2;
static bool buzzer_alarm = false;
static char ip_addr_str[16] = {0};
static int eeprom_addr = 0x00;
spi_device_handle_t spi_device;
spi_host_device_t masterHostId = VSPI_HOST;
SSD1306_t dev;
mq5_t mq5;

// Data structure to send to the server
typedef struct
{
    float temperature;
    uint8_t humidity;
    float mq5;
} data_t;


// Configure GPIO pins and stop the rover
static void setup_pins(void)
{
    // Set up the GPIO pins to control the relay module
    gpio_set_direction(RELAY_1, GPIO_MODE_OUTPUT);
    gpio_set_direction(RELAY_2, GPIO_MODE_OUTPUT);
    gpio_set_direction(RELAY_3, GPIO_MODE_OUTPUT);
    gpio_set_direction(RELAY_4, GPIO_MODE_OUTPUT);
    gpio_set_direction(BUZZER_PIN, GPIO_MODE_OUTPUT);

    // Stop the rover
    stop();
}

// Event handler for IP address acquisition
static void on_got_ip(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data)
{
    ip_event_got_ip_t *event = (ip_event_got_ip_t *)event_data;
    // esp_netif_t *netif = event->esp_netif;
    ESP_LOGI(TAG, "Got IP: " IPSTR, IP2STR(&event->ip_info.ip));

    // Convert IP address to string
    snprintf(ip_addr_str, 16, IPSTR, IP2STR(&event->ip_info.ip));
}

// Event handler for Wi-Fi disconnection
static void on_wifi_disconnect(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data)
{
    ESP_LOGI(TAG, "Wi-Fi disconnected. Reconnecting...");
    esp_wifi_connect();
}

// Initialize Wi-Fi
static void wifi_init(void)
{
    esp_netif_init();
    esp_event_loop_create_default();
    esp_netif_t *sta_netif = esp_netif_create_default_wifi_sta();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    esp_wifi_init(&cfg);
    esp_wifi_set_mode(WIFI_MODE_STA);
    esp_wifi_set_config(ESP_IF_WIFI_STA, &(wifi_config_t){.sta = {
                                         .ssid = WIFI_SSID,
                                         .password = WIFI_PASS,
                                     }});

    esp_wifi_start();
    esp_wifi_connect();

    // Register the IP_EVENT_STA_GOT_IP event handler
    // esp_event_handler_instance_t instance;
    esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, on_got_ip, NULL);

    TickType_t start_time = xTaskGetTickCount();
    TickType_t timeout = pdMS_TO_TICKS(10000);
    TickType_t current_time;

    // Wait for the IP address to be acquired
    while (strlen(ip_addr_str) == 0) {
        // Check if the timeout has occurred
        current_time = xTaskGetTickCount();
        if ((current_time - start_time) >= timeout) break;
        vTaskDelay(100 / portTICK_PERIOD_MS);
    }

    // Initialize the OLED display
	esp_err_t ret_ssd1306 = ssd1306_init(&dev, SDA_PIN, SCL_PIN, 128, 32);
    if (ret_ssd1306 != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize SSD1306 device");
        return;
    }
    ESP_LOGI(TAG, "SSD1306 device initialized successfully");

    // Display the IP address on the OLED display
    ssd1306_clear_screen(&dev);
    ssd1306_display_text(&dev, 0, "Server IP:", 10);
	ssd1306_display_text(&dev, 1, SERVER_IP_ADDR, strlen(SERVER_IP_ADDR));
	ssd1306_display_text(&dev, 2, "Rover IP:", 9);
    ssd1306_display_text(&dev, 3, ip_addr_str, strlen(ip_addr_str));

    // Register the WIFI_EVENT_STA_DISCONNECTED event handler
    // esp_event_handler_instance_t instance_disconnect;
    esp_event_handler_register(WIFI_EVENT, WIFI_EVENT_STA_DISCONNECTED, on_wifi_disconnect, NULL);
}

// Initialize the HTTP server
static void http_server_init(void)
{
    httpd_handle_t server = NULL;
    httpd_config_t config = HTTPD_DEFAULT_CONFIG();

    // Set the listening port to 80
    config.server_port = 80;

    httpd_uri_t control_uri = {
        .uri = "/control",
        .method = HTTP_POST,
        .handler = control_handler,
        .user_ctx = NULL,
    };

    httpd_uri_t ping_uri = {
        .uri = "/ping",
        .method = HTTP_HEAD,
        .handler = ping_handler,
        .user_ctx = NULL,
    };

    if (httpd_start(&server, &config) == ESP_OK) {
        httpd_register_uri_handler(server, &control_uri);
        httpd_register_uri_handler(server, &ping_uri);
    }
}

// HTTP request handler for controlling the rover
static esp_err_t control_handler(httpd_req_t *req)
{
    // Buffer to store the received data
    char buffer[100];

    // Receive the data from the request
    int ret = httpd_req_recv(req, buffer, sizeof(buffer) - 1);

    // If there's an error, return ESP_FAIL
    if (ret <= 0) return ESP_FAIL;

    // Null-terminate the received string
    buffer[ret] = '\0';

    // Check the command and execute the corresponding function
    if (strstr(buffer, "forward") != NULL) {
        forward();
        ESP_LOGI(TAG, "Forward");
    } else if (strstr(buffer, "backward") != NULL) {
        backward();
        ESP_LOGI(TAG, "Backward");
    } else if (strstr(buffer, "right") != NULL) {
        right();
        ESP_LOGI(TAG, "Right");
    } else if (strstr(buffer, "left") != NULL) {
        left();
        ESP_LOGI(TAG, "Left");
    } else if (strstr(buffer, "stop") != NULL) {
        stop();
        ESP_LOGI(TAG, "Stop");
    }

    // Send the response
    httpd_resp_sendstr(req, "OK");
    return ESP_OK;
}

// HTTP request handler for pinging the server
static esp_err_t ping_handler(httpd_req_t *req)
{
    // Send an empty response with a 200 status code
    httpd_resp_send(req, NULL, 0);
    ESP_LOGI(TAG, "Ping received");
    return ESP_OK;
}

// Send a POST request to the server with the temperature, humidity and gases readings
void send_data(void *pvParameters)
{
    data_t *data = (data_t *) pvParameters;

    esp_http_client_config_t config = {
        .url = "http://"  SERVER_IP_ADDR  ":5000/dht11"
    };

    esp_http_client_handle_t client = esp_http_client_init(&config);
    // Set the HTTP method to POSTs
    esp_http_client_set_method(client, HTTP_METHOD_POST);

    // Create a JSON object
    cJSON *root = cJSON_CreateObject();

    // Add temperature and humidity to the object
    cJSON_AddNumberToObject(root, "temperature", data->temperature);
    cJSON_AddNumberToObject(root, "humidity", data->humidity);
    cJSON_AddNumberToObject(root, "mq5", data->mq5);

    // Convert the JSON object to a string
    char *json_string = cJSON_Print(root);
    if (json_string == NULL) {
        ESP_LOGE(TAG, "Failed to print JSON");
    } else {
        esp_http_client_set_header(client, "Content-Type", "application/json");
        // Set the JSON string as the body of the POST request
        esp_http_client_set_post_field(client, json_string, strlen(json_string));

        // Perform the HTTP POST request
        esp_err_t err = esp_http_client_perform(client);

        if (err == ESP_OK) {
            ESP_LOGI(TAG, "HTTP POST Status = %d, content_length = %lld",
                    esp_http_client_get_status_code(client),
                    esp_http_client_get_content_length(client));
        } else {
            ESP_LOGE(TAG, "HTTP POST request failed: %s", esp_err_to_name(err));
        }

        // Clean up
        free(json_string);
    }

    // Delete the JSON object
    cJSON_Delete(root);

    // Cleanup
    esp_http_client_cleanup(client);
    free(data);
    vTaskDelete(NULL);
}

// Stop the rover by setting all relays to high
static void stop(void)
{
    gpio_set_level(RELAY_1, 1);
    gpio_set_level(RELAY_2, 1);
    gpio_set_level(RELAY_3, 1);
    gpio_set_level(RELAY_4, 1);
}

// Move the rover forward
static void forward(void)
{
    gpio_set_level(RELAY_1, 1);
    gpio_set_level(RELAY_2, 0);
    gpio_set_level(RELAY_3, 0);
    gpio_set_level(RELAY_4, 1);
}

// Move the rover backwards
static void backward(void)
{
    gpio_set_level(RELAY_1, 0);
    gpio_set_level(RELAY_2, 1);
    gpio_set_level(RELAY_3, 1);
    gpio_set_level(RELAY_4, 0);
}

// Turn the rover right
static void right(void)
{
    gpio_set_level(RELAY_1, 1);
    gpio_set_level(RELAY_2, 0);
    gpio_set_level(RELAY_3, 0);
    gpio_set_level(RELAY_4, 0);
}

// Turn the rover left
static void left(void)
{
    gpio_set_level(RELAY_1, 0);
    gpio_set_level(RELAY_2, 0);
    gpio_set_level(RELAY_3, 0);
    gpio_set_level(RELAY_4, 1);
}

// Obtain the current time from the NTP server
void obtain_time(void)
{
    ESP_LOGI(TAG, "Initializing SNTP");
    sntp_setoperatingmode(SNTP_OPMODE_POLL);
    sntp_setservername(0, "pool.ntp.org");
    sntp_init();

    time_t now = 0;
    struct tm timeinfo = { 0 };
    int retry = 0;
    const int retry_count = 10;
    while (sntp_get_sync_status() == SNTP_SYNC_STATUS_RESET && ++retry < retry_count) {
        ESP_LOGI(TAG, "Waiting for system time to be set... (%d/%d)", retry, retry_count);
        vTaskDelay(2000 / portTICK_PERIOD_MS);
    }
    time(&now);
    localtime_r(&now, &timeinfo);

    // Print the current time and write it to a file
    char strftime_buf[64];
    strftime(strftime_buf, sizeof(strftime_buf), "%c", &timeinfo);
    ESP_LOGI(TAG, "The current date/time is: %s", strftime_buf);
    spiffs_write_file("/spiffs/temp.txt", strftime_buf);
    spiffs_write_file("/spiffs/humid.txt", strftime_buf);
    spiffs_write_file("/spiffs/gas.txt", strftime_buf);
}

// Callback function for the timer that reads the sensor values
static void sensor_readings_callback(void *arg)
{
    struct dht11_data_t data;
    float mq5_value = 0.0f;
    float temperature = 0.0f;
    data_t *send = malloc(sizeof(data_t));
    esp_err_t ret;

    mq5_value = mq5_read(&mq5, mq5_read_ratio(&mq5));
    ESP_LOGI (TAG, "MQ-5 value: %f ppm", mq5_value);
    send->mq5 = mq5_value;

    if (dht11_read(&data) == ESP_OK) {
        ESP_LOGI(TAG, "Humidity: %d %% Temperature: %d.%d ºC", data.humidity, data.temperature, data.temperature_decimal);
        temperature = data.temperature + ((float)data.temperature_decimal / 10.0f);
        send->temperature = temperature;
        send->humidity = data.humidity;
        xTaskCreate(send_data, "send_data", configMINIMAL_STACK_SIZE * 5, (void*) send, 5, NULL);
    } else {
        ESP_LOGE(TAG, "Could not read data from DHT11 sensor status code: %d", data.status);
    }

    if (mq5_value > 1000) {
        buzzer_alarm = true;
    } else {
        buzzer_alarm = false;
    }

    // ret = eeprom_write_byte(spi_device, eeprom_addr++, data.temperature);
    // ESP_LOGI(TAG, "EEPROM Write Temperature: %d", data.temperature);
    // if (ret != ESP_OK) {
    //     ESP_LOGE(TAG, "Failed to write byte");
    //     return;
    // }
    // vTaskDelay(1000 / portTICK_PERIOD_MS);
    // ret = eeprom_write_byte(spi_device, eeprom_addr++, data.humidity);
    // ESP_LOGI(TAG, "EEPROM Write Humidity: %d", data.humidity);
    // if (ret != ESP_OK) {
    //     ESP_LOGE(TAG, "Failed to write byte");
    //     return;
    // }
    // vTaskDelay(1000 / portTICK_PERIOD_MS);
    // ret = eeprom_write_byte(spi_device, eeprom_addr++, ((int) mq5_value) / 40);
    // ESP_LOGI(TAG, "EEPROM Write Gas: %d", ((int) mq5_value) / 40);
    // if (ret != ESP_OK) {
    //     ESP_LOGE(TAG, "Failed to write byte");
    //     return;
    // }

    // ESP_LOGI(TAG, "EEPROM address: %d", eeprom_addr);

    // if (eeprom_addr == 512) {
    //     eeprom_addr = 0;
    //     ESP_LOGI(TAG, "EEPROM address reset");
    // }

    // Storing the values in SPIFFS
    char temp_string[20];
    char gas_string[20];
    char humid_string[4];

    sprintf(temp_string, "%.1f", temperature);
    sprintf(humid_string, "%u", data.humidity);
    sprintf(gas_string, "%f", mq5_value);

    spiffs_write_file(SPIFFS_TEMP_PATH, temp_string);
    spiffs_write_file(SPIFFS_HUMID_PATH, humid_string);
    spiffs_write_file(SPIFFS_GAS_PATH, gas_string);
}


void app_main(void)
{
    // Initialize NVS, required for Wi-Fi
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK(ret);

    // Initialize Wi-Fi
    wifi_init();

    // Initialize HTTP server
    http_server_init();
    
    // Set up rover and GPIOs
    setup_pins();

    esp_vfs_spiffs_conf_t conf = {
      .base_path = "/spiffs",
      .partition_label = NULL,
      .max_files = 5,
      .format_if_mount_failed = true
    };
    
    // Initialize SPIFFS
    esp_err_t spiffs_result = spiffs_mount(&conf);
    if (spiffs_result == ESP_OK) {
        ESP_LOGI(TAG, "SPIFFS mounted successfully");
    } else {
        return;
    }
    spiffs_get_info(&conf);

    // Initialize SPI device E2PROM 25LC040A
    esp_err_t ret_eeprom = eeprom_init(masterHostId, CS_PIN, SCK_PIN, MOSI_PIN, MISO_PIN, CLK_SPEED_HZ, &spi_device);
    if (ret_eeprom != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize EEPROM device: %d", ret_eeprom);
        return;
    }
    ESP_LOGI(TAG, "EEPROM device initialized successfully");

    // Write in status register on E2PROM 25LC040A to enable writing on all memory
    ret = eeprom_write_status(spi_device, 0x00); 
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to write in status");
        return;
    }

    // Initilizae the DHT11 sensor (doesn't require drivers)
    dht11_init(DHT11_PIN);
    ESP_LOGI(TAG, "DHT11 sensor initialized successfully");

    // Initialize the MQ-5 sensor (doesn't require drivers)
    mq5_init(&mq5, MQ5_PIN, ADC_WIDTH_BIT_12, ADC_ATTEN_DB_11, A, B, RL);
    ESP_LOGI(TAG, "MQ-5 sensor initialized successfully");

    // Initialize Piezo Buzzer
    esp_err_t ret_buzz = buzzer_init(BUZZER_PIN);
    if (ret_buzz != ESP_OK) {
        ESP_LOGE(TAG, "Failed to initialize Piezo Buzzer: %d", ret_buzz);
        return;
    }
    ESP_LOGI(TAG, "Piezo Buzzer initialized successfully");

    // Stop the buzzer (if not done it will play a continuous sound)
    ret_buzz = buzzer_stop();
    if (ret_buzz != ESP_OK) {
        ESP_LOGE(TAG, "Failed to stop Piezo Buzzer: %d", ret_buzz);
        return;
    }
    ESP_LOGI(TAG, "Piezo Buzzer stopped successfully");
    
    // Obtain the current time from the NTP server
    obtain_time();
    
    // Create a timer to read the sensor values every 3 seconds
    const esp_timer_create_args_t readings_timer_args = {
            .callback = &sensor_readings_callback,
            .name = "readings_timer"
    };
    esp_timer_handle_t readings_timer;
    ESP_ERROR_CHECK(esp_timer_create(&readings_timer_args, &readings_timer));
    ESP_ERROR_CHECK(esp_timer_start_periodic(readings_timer, 3000000));

    // Play the buzzer alarm every 100 ms
    int alarm_state = 0;
    while (1) {
        alarm_state = buzzer_play(buzzer_alarm, alarm_state);
        vTaskDelay(100 / portTICK_PERIOD_MS);
    }

    // Free SPI device E2PROM 25LC040A
    esp_err_t free_result = eeprom_free(masterHostId, spi_device);
    if (free_result == ESP_OK) {
        ESP_LOGI(TAG, "EEPROM device freed succeeded");
    } else {
        ESP_LOGE(TAG, "EEPROM devied failed free");
    }

    // Free I2C device SSD1306
    free_result = ssd1306_free(&dev);
    if (free_result == ESP_OK) {
        ESP_LOGI(TAG, "SSD1306 device freed succeeded");
    } else {
        ESP_LOGE(TAG, "SSD1306 devied failed free");
    }

    // Unmount SPIFFS
    spiffs_result = spiffs_unmount(&conf);
    if (spiffs_result == ESP_OK) {
        ESP_LOGI(TAG, "SPIFFS unmounted successfully");
    } else {
        ESP_LOGE(TAG, "SPIFFS unmount failed");
        return;
    }
}
