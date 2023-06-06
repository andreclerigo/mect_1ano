set(component_srcs "dht11.c")

idf_component_register(SRCS "${component_srcs}"
                       PRIV_REQUIRES driver esp_timer
                       INCLUDE_DIRS ".")