#ifndef MQ5_H_
#define MQ5_H_

#include "driver/adc.h"

#define RATIO_MQ5_CLEAN_AIR 6.5   // RS/R0 = 6.5 
#define VOLTAGE_RANGE 3.3         // ESP32 ADC voltage range

typedef struct {
    adc1_channel_t channel;
    adc_bits_width_t width;
    adc_atten_t atten;
    float a;
    float b;
    float rl;
    float r0;
} mq5_t;

void mq5_init(mq5_t *data, int channel, adc_bits_width_t width, adc_atten_t atten, float a, float b, float rl);
float mq5_read_ratio(mq5_t *data);
float mq5_read(mq5_t *data, float ratio);

#endif // MQ5_H