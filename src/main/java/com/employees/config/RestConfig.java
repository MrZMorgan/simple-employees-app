package com.employees.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        final var restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(setBasicAuth());
        return restTemplate;
    }

    private ClientHttpRequestInterceptor setBasicAuth() {
        return (request, body, execution) -> {
            request.getHeaders().setBasicAuth("test", "test");
            return execution.execute(request, body);
        };
    }
}
