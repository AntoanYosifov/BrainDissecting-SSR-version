package com.antdevrealm.braindissectingssrversion.config;


import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "doaj.api")
public class ApiConfig {

    private String url;

    public ApiConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    @PostConstruct
    public void checkConfiguration() {
        verifyNotNullOrEmpty(url);
    }

    private static void verifyNotNullOrEmpty(String propertyValue) {
        if (propertyValue == null || propertyValue.isBlank()) {
            throw new IllegalArgumentException("Property url cannot be empty.");
        }
    }
}
