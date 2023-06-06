#include "driver/ledc.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "buzzer.h"

#define LEDC_HS_TIMER LEDC_TIMER_0
#define LEDC_HS_MODE LEDC_HIGH_SPEED_MODE
#define LEDC_HS_CH0_CHANNEL LEDC_CHANNEL_0


/**
 * @brief Play the alarm sound.
 * 
 * @param alarm_state the current state of the alarm for sound commutation
 * @return int the new state of the alarm for sound commutation
 */
int _buzzer_play_alarm(int alarm_state)
{
    ledc_set_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL, 512);
    ledc_update_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL);
    if(alarm_state == 0) {
        ledc_set_freq(LEDC_HS_MODE, LEDC_HS_TIMER, 2000);
        alarm_state = 1;
    } else {
        ledc_set_freq(LEDC_HS_MODE, LEDC_HS_TIMER, 1000);
        alarm_state = 0;
    }
    vTaskDelay(500 / portTICK_PERIOD_MS);

    return alarm_state;
}

/**
 * @brief Initialize the buzzer.
 * 
 * @param gpio_num the pin number for the buzzer
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t buzzer_init(gpio_num_t gpio_num)
{
    ledc_timer_config_t ledc_timer = {
        .duty_resolution = LEDC_TIMER_10_BIT,
        .freq_hz = 2000,
        .speed_mode = LEDC_HS_MODE,
        .timer_num = LEDC_HS_TIMER
    };
    esp_err_t ret = ledc_timer_config(&ledc_timer);
    if (ret != ESP_OK) {
        return ret;
    }

    ledc_channel_config_t ledc_channel = {
        .channel = LEDC_HS_CH0_CHANNEL,
        .duty = 512,
        .gpio_num = gpio_num,
        .speed_mode = LEDC_HS_MODE,
        .timer_sel = LEDC_HS_TIMER
    };
    return ledc_channel_config(&ledc_channel);
}

/**
 * @brief Play the buzzer, normal hearbeat if alarm is false, alarm sound if alarm is true.
 * 
 * @param alarm true if alarm is on, false if alarm is off
 * @param alarm_state the current state of the alarm for sound commutation
 * @return int the new state of the alarm for sound commutation
 */
int buzzer_play(bool alarm, int alarm_state)
{
    if (alarm) {
        return _buzzer_play_alarm(alarm_state);
    } else {
        ledc_set_freq(LEDC_HS_MODE, LEDC_HS_TIMER, 2000);
        ledc_set_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL, 512);
        ledc_update_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL);
        vTaskDelay(100 / portTICK_PERIOD_MS);
        ledc_set_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL, 0);
        ledc_update_duty(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        return 0;
    }
}

/**
 * @brief Stop the buzzer, it prevents the buzzer from playing continuously.
 * 
 * @return esp_err_t either ESP_OK if the operation was successful or ESP_FAIL if it failed
 */
esp_err_t buzzer_stop()
{
    return ledc_stop(LEDC_HS_MODE, LEDC_HS_CH0_CHANNEL, 0);
}
