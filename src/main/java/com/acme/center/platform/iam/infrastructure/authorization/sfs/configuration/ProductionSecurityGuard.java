package com.acme.center.platform.iam.infrastructure.authorization.sfs.configuration;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/** Fail startup rather than allowing the production profile with security disabled. */
@Configuration
@Profile("prod")
public class ProductionSecurityGuard {
    @Bean
    SmartInitializingSingleton enforceProductionSecurity(Environment environment) {
        return () -> {
            if (!environment.getProperty("app.security.enabled", Boolean.class, true)) {
                throw new IllegalStateException("Security must be enabled in the prod profile");
            }
        };
    }
}
