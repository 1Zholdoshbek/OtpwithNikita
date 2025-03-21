package com.tashiev.otpwithnikita.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Otp with Nikita API",
                description = "Otp with Nikita Api documentation",
                version = "1.0",
                termsOfService = "Terms of service"
        ),

        servers = {
                @Server(
                        description = "Local env",
                        url = "http://localhost:9898"
                ),
        },

        security = @SecurityRequirement(name = "bearerAuth")
)
//@SecurityScheme(
//        name = "bearerAuth",
//        description = "JWT auth description",
//        scheme = "bearer",
//        type = SecuritySchemeType.HTTP,
//        bearerFormat = "JWT",
//        in = SecuritySchemeIn.HEADER
//)
public class OpenAPIInfo {}
