package com.treat.tinderdog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Tinder app for dogs", version = "v1"))
@SecurityScheme(
        name = "JSESSIONID",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE
)
public class SwaggerConfiguration {
}
