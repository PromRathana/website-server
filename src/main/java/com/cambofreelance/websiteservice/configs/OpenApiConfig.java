package com.cambofreelance.websiteservice.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info()
        .title("Keycloak Authentication Server").version("1.0.0")
        .description("This is a sample Spring Boot REST API with OpenAPI documentation")
        .contact(new Contact().name("Cambofreelance").email("cambofreelance@gmail.com")
            .url("https://cambofreelance.com")).license(new License().name("Cambofreelance 1.1")
            .url("https://cambofreelance.com")));
  }
}
