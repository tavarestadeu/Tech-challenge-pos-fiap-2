package com.br.fiap.desafio_tech_2.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI gestaoRestaurante(){
        return new OpenAPI().info(
                new Info().title("Gestão de restaurantes API")
                        .description("API de controle de gestão de restaurantes para donos e clientes")
                        .version("V1.0.0")
                        .license(new License().name("Apache 2.0").url("https://gitbug.com/tadeu")));

    }
}