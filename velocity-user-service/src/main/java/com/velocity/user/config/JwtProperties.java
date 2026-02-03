package com.velocity.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * JWT configuration properties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "VeloCity2026SecretKeyForJWTTokenGenerationAndValidationPleaseChangeInProduction";
    private Long accessTokenExpiration = 900000L; // 15 minutes in milliseconds
    private Long refreshTokenExpiration = 604800000L; // 7 days in milliseconds
    private String issuer = "VeloCity";
}
