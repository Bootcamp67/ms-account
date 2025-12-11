package com.bootcamp67.ms_account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient msCustomerWebClient(@Value("${ms.customer.base-url}") String baseUrl){
    return WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }
}
