#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"
#include "driver/gpio.h"
#include "esp_timer.h"
#include "sdkconfig.h"


static const char *TAG = "FREQ_GAME_TIMER";
static uint8_t OUT_GPIO = 5;
static uint8_t DEBUG_GPIO = 19;
static uint8_t IN_GPIO = 12;
static int OUT_FREQ = 500000;
static int IN_FREQ = 100;     // 0,1ms
static bool state = false;
static bool window[1000];
static int w_index = 0;
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
    while (1) {
        if (polling_signal == 1) {

            int counter = 0;
            for (int i = 0; i < 1000; i++) {
                counter += window[i];
            }

            ESP_LOGI(TAG, "Counter is %d", counter);

            int frequency = 100;

            polling_signal = 0;
            printf("\nEnd of Polling, frequency is %d Hz\n", frequency);
            printf("esp32> ");
        }
        
        vTaskDelay(pdMS_TO_TICKS(10));
    }

    vTaskDelete(NULL);
}

void console_task(void *pvParameters)
{
    esp_timer_handle_t periodic_timer = (esp_timer_handle_t)pvParameters;
    char input[6];      // out, in, help
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
                printf("\nesp32> Choose the frequency (Hz) to output (1-10000)\n");
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

                if (freq <= 0 || freq > 10000) {
                    printf("\nInvalid frequency!\n");
                    reset_buffer(input, sizeof(input), &i);
                    continue;
                }

                // convert num (hz) to ms
                OUT_FREQ = 1000*1000/freq/2;

                printf("\nOutputing %i Hz\n", freq);
                reset_buffer(input, sizeof(input), &i);
                option = 0;

                ESP_ERROR_CHECK(esp_timer_stop(periodic_timer));
                ESP_ERROR_CHECK(esp_timer_start_periodic(periodic_timer, OUT_FREQ));
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

static void output_frequency_callback(void* arg)
{
    gpio_set_level(OUT_GPIO, state);
    gpio_set_level(DEBUG_GPIO, state);
    state = !state;
}

static void input_sampling_callback(void* arg)
{
    window[w_index] = gpio_get_level(IN_GPIO);
    w_index = (w_index + 1) % 1000;
}

void app_main(void)
{
    configure_pins();
    
    for (int i = 0; i < 1000; i++) {
        window[i] = false;
    }

    const esp_timer_create_args_t output_timer_args = {
            .callback = &output_frequency_callback,
            .name = "output_timer"
    };
    esp_timer_handle_t output_timer;
    ESP_ERROR_CHECK(esp_timer_create(&output_timer_args, &output_timer));
    ESP_ERROR_CHECK(esp_timer_start_periodic(output_timer, OUT_FREQ));

    vTaskDelay(pdMS_TO_TICKS(33)); // Wait for 1 second to avoid debug print after the prompt

    const esp_timer_create_args_t input_timer_args = {
            .callback = &input_sampling_callback,
            .name = "input_timer"
    };
    esp_timer_handle_t input_timer;
    ESP_ERROR_CHECK(esp_timer_create(&input_timer_args, &input_timer));
    ESP_ERROR_CHECK(esp_timer_start_periodic(input_timer, IN_FREQ));
    
    xTaskCreate(console_task, "console", 4096, (void*) output_timer, 10, NULL);
    xTaskCreate(frequency_measurement_task, "frequency_measurement", 4096, NULL, 10, NULL);
    
    // Block the main thread forever
    while (true) {
        vTaskDelay(pdMS_TO_TICKS(2000));
    }
}
