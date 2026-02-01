package com.woodstove.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class JwtConfig {

    @Value("${woodstove.google.client-id:}")
    private String googleClientId;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation("https://accounts.google.com");

        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");

        OAuth2TokenValidator<Jwt> audienceValidator = token -> {
            if (googleClientId.isEmpty()) {
                return OAuth2TokenValidatorResult.success();
            }
            if (token.getAudience().contains(googleClientId)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Invalid audience", null)
            );
        };

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators, audienceValidator));

        return jwtDecoder;
    }
}
