#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"
#include "driver/gpio.h"


static const char *TAG = "FREQ_GAME";
static uint8_t OUT_GPIO = 5;
static uint8_t DEBUG_GPIO = 19;
static uint8_t IN_GPIO = 12;
static int OUT_FREQ = 500;
static volatile uint8_t polling_signal = 0;

static void configure_pins(void)
{
    ESP_LOGI(TAG, "Example configured to GPIO 5 as output and GPIO 12 as input!");
    gpio_reset_pin(OUT_GPIO);
    gpio_set_direction(OUT_GPIO, GPIO_MODE_OUTPUT);

    gpio_reset_pin(DEBUG_GPIO);
    gpio_set_direction(DEBUG_GPIO, GPIO_MODE_OUTPUT);

    gpio_reset_pin(IN_GPIO);
    gpio_set_direction(IN_GPIO, GPIO_MODE_INPUT);
}

void reset_buffer(char *buffer, size_t size, int *i)
{
    *i = 0;                  // Reset index
    memset(buffer, 0, size); // Reset buffer

    // Ask for the user's name again
    printf("esp32> ");
}

void frequency_measurement_task(void *pvParameters)
{
    uint32_t start_time;
    uint32_t current_time;

    while (1) {
        vTaskDelay(pdMS_TO_TICKS(10));
        
        if (polling_signal == 1) {
            while (gpio_get_level(IN_GPIO) == 0);
            
            start_time = xTaskGetTickCountFromISR();

            while (gpio_get_level(IN_GPIO) == 1);

            while (gpio_get_level(IN_GPIO) == 0);

            current_time = xTaskGetTickCountFromISR();

            uint32_t frequency = 1000 / (current_time - start_time) / 10;   // 1 tickCount is 10ms

            polling_signal = 0;
            printf("\nEnd of Polling, frequency is %lu Hz\n", frequency);
            printf("esp32> ");
        }
    }

    vTaskDelete(NULL);
}

void console_task(void *pvParameters)
{
    char input[5];      // out, in, help
    int option = 0;
    int i = 0;
    char c;

    vTaskDelay(pdMS_TO_TICKS(100)); // Wait for 100 ms to avoid debug print after the prompt
    reset_buffer(input, sizeof(input), &i);

    while (1) {
        // Wait for user input (character by character) and using stdin
        while(scanf("%c", &c) != 1) {
            vTaskDelay(pdMS_TO_TICKS(100)); // Wait for 100 ms
        }
        printf("%c", c);

        // If the user presses enter, print the input
        if(c == '\n') {
            if (strcmp(input, "help") == 0) {
                printf("\nout - output the current frequency\nin - input a new frequency\nexit - exit the program\n");
                reset_buffer(input, sizeof(input), &i);
                option = 0;
                continue;
            }

            if (strcmp(input, "out") == 0) {
                printf("\nesp32> Choose the frequency (Hz) to output (1-50)\n");
                reset_buffer(input, sizeof(input), &i);
                option = 1;
                continue;
            }

            if (strcmp(input, "in") == 0) {
                polling_signal = 1;
                reset_buffer(input, sizeof(input), &i);
                option = 0;
                continue;
            }

            if (option == 1) {
                int freq = strtol(input, NULL, 10);

                if (freq <= 0 || freq > 50) {
                    printf("\nInvalid frequency!\n");
                    reset_buffer(input, sizeof(input), &i);
                    continue;
                }

                // convert num (hz) to ms
                OUT_FREQ = 1000/freq/2;

                printf("\nOutputing %i Hz\n", freq);
                reset_buffer(input, sizeof(input), &i);
                option = 0;
            } else {
                printf("\nInvalid command (use help)!\n");
                reset_buffer(input, sizeof(input), &i);
                option = 0;
            }
        } else {
            if (i >= sizeof(input) - 1) {
                if (option == 1)  {
                    printf("\nThe number is too long!\n");
                    reset_buffer(input, sizeof(input), &i);
                    continue;
                } else {
                    printf("\nInvalid command (use help)!\n");
                    reset_buffer(input, sizeof(input), &i);
                    option = 0;
                    continue;
                }
            }
            
            // Add the character to the end of the input
            input[i++] = c;
        }
    }

    vTaskDelete(NULL);
}

void output_task(void *pvParameters)
{
    while (1) {
        gpio_set_level(OUT_GPIO, 1);
        gpio_set_level(DEBUG_GPIO, 1);
        vTaskDelay(pdMS_TO_TICKS(OUT_FREQ));
        gpio_set_level(OUT_GPIO, 0);
        gpio_set_level(DEBUG_GPIO, 0);
        vTaskDelay(pdMS_TO_TICKS(OUT_FREQ));
    }

    vTaskDelete(NULL);
}

void app_main(void)
{
    configure_pins();

    xTaskCreate(console_task, "console", 4096, NULL, 10, NULL);
    xTaskCreate(output_task, "output", 4096, NULL, 10, NULL);
    xTaskCreate(frequency_measurement_task, "freq_meas", 4096, NULL, 10, NULL);
}
