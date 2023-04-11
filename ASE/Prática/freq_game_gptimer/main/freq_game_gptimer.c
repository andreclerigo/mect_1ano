#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "driver/gpio.h"
#include "freertos/queue.h"
#include "driver/gptimer.h"
#include "esp_log.h"


static const char *TAG = "FREQ_GAME_GPTIMER";
static uint8_t OUT_GPIO = 5;
static uint8_t DEBUG_GPIO = 19;
static uint8_t IN_GPIO = 12;
static int TIMER_RESOLUTION = 1000000;          // 1MHz
static int OUT_PERIOD = 500000;                 // 0.5s
static bool state = 0;
static volatile uint8_t polling_signal = 0;

typedef struct {
    uint64_t event_count;
} queue_element_t;


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
                printf("\nesp32> Choose the frequency (Hz) to output (1-5000)\n");
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

                if (freq <= 0 || freq > 50000) {
                    printf("\nInvalid frequency!\n");
                    reset_buffer(input, sizeof(input), &i);
                    continue;
                }

                // convert hz to period
                OUT_PERIOD = TIMER_RESOLUTION/freq/2;
                ESP_LOGI(TAG, "OUT_PERIOD: %i", OUT_PERIOD);

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

static bool IRAM_ATTR output_frequency_alarm(gptimer_handle_t timer, const gptimer_alarm_event_data_t *edata, void *user_data)
{
    BaseType_t high_task_awoken = pdFALSE;
    QueueHandle_t queue = (QueueHandle_t)user_data;

    queue_element_t ele = {
        .event_count = edata->count_value
    };

    gptimer_alarm_config_t alarm_config = {
        .alarm_count = edata->alarm_value + OUT_PERIOD,
    };
    gptimer_set_alarm_action(timer, &alarm_config);
    xQueueSendFromISR(queue, &ele, &high_task_awoken);

    return high_task_awoken == pdTRUE;
}

static bool IRAM_ATTR input_frequency_alarm(gptimer_handle_t timer, const gptimer_alarm_event_data_t *edata, void *user_data)
{
    BaseType_t high_task_awoken = pdFALSE;
    QueueHandle_t queue = (QueueHandle_t)user_data;

    queue_element_t ele = {
        .event_count = edata->count_value
    };
    xQueueSendFromISR(queue, &ele, &high_task_awoken);

    return high_task_awoken == pdTRUE;
}

static void output_frequency_task(void *pvParameters)
{
    QueueHandle_t queue = (QueueHandle_t)pvParameters;
    queue_element_t ele;

    while (1) {
        if (xQueueReceive(queue, &ele, pdMS_TO_TICKS(0))) {
            gpio_set_level(DEBUG_GPIO, state);
            gpio_set_level(OUT_GPIO, state);
            state = !state;
        }

        vTaskDelay(pdMS_TO_TICKS(10));
    }

    vTaskDelete(NULL);
}

void app_main(void)
{
    configure_pins();

    QueueHandle_t queue = xQueueCreate(10, sizeof(queue_element_t));
    if (!queue) {
        ESP_LOGE(TAG, "Creating queue1 failed");
        return;
    }

    gptimer_handle_t gptimer = NULL;
    gptimer_config_t timer_config = {
        .clk_src = GPTIMER_CLK_SRC_DEFAULT,
        .direction = GPTIMER_COUNT_UP,
        .resolution_hz = TIMER_RESOLUTION,
    };
    ESP_ERROR_CHECK(gptimer_new_timer(&timer_config, &gptimer));

    gptimer_event_callbacks_t cbs = { .on_alarm = output_frequency_alarm };
    ESP_ERROR_CHECK(gptimer_register_event_callbacks(gptimer, &cbs, queue));
    ESP_ERROR_CHECK(gptimer_enable(gptimer));

    gptimer_alarm_config_t alarm_config = { .reload_count = 0, .alarm_count = OUT_PERIOD, .flags.auto_reload_on_alarm = true };
    ESP_ERROR_CHECK(gptimer_set_alarm_action(gptimer, &alarm_config));
    ESP_ERROR_CHECK(gptimer_start(gptimer));

    xTaskCreate(console_task, "console", 4096, NULL, 10, NULL);
    xTaskCreate(output_frequency_task, "output", 4096, queue, 10, NULL);

    // Block the main thread forever
    while (true) {
        vTaskDelay(pdMS_TO_TICKS(1000));
    }
}
