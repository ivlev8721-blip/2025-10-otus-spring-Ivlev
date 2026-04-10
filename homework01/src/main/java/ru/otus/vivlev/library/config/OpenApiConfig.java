package ru.otus.vivlev.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VinylLib API by V.I. Ivlev")
                        .version("1.0")
                        .description("REST API для управления коллекцией виниловых пластинок. " +
                                "Включает управление альбомами, жанрами, отзывами, пользовательскими коллекциями и вишлистами. " +
                                "Поддерживает импорт/экспорт данных в CSV формате.")
                        .contact(new Contact()
                                .name("VinylLib Support")
                                .email("vivlev8721@mail.ru")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }
}
