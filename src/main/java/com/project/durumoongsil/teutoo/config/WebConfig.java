package com.project.durumoongsil.teutoo.config;

import com.project.durumoongsil.teutoo.common.SpringSecurityAuditorAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public SpringSecurityAuditorAware auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
