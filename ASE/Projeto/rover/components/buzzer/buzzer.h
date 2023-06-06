#ifndef BUZZER_H_
#define BUZZER_H_

#include "driver/ledc.h"


esp_err_t buzzer_init(gpio_num_t gpio_num);
int buzzer_play(bool alarm, int alarm_state);
esp_err_t buzzer_stop();

#endif // BUZZER_H_
