package com.smart.bugrca.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart Bug RCA API",
                version = "1.0",
                description = "Bug Management System with Smart Root Cause Analysis"
        )
)
public class SwaggerConfig {
}