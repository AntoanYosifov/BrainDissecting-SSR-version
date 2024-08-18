package com.antdevrealm.braindissectingssrversion.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class Config {

    @Bean
    public ModelMapper getModelMapper () {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
