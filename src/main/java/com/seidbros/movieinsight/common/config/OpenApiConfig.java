package com.seidbros.movieinsight.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MovieInsight API",
        version = "1.0",
        description = "API documentation for MovieInsight application"

    ), security = @SecurityRequirement(name = "Bearer Authentication")

)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Provide the JWT token in the 'Value' field with 'Bearer ' prefix. Example: 'Bearer eyJ...'"
)
public class OpenApiConfig {
}