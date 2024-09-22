package com.antdevrealm.braindissectingssrversion.config;

import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.impl.BrDissectingUserDetailService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

       return httpSecurity
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .requestMatchers("/", "/users/login", "/users/register")
                                        .permitAll()
                                        .requestMatchers("/admin/manage-roles").hasRole("ADMIN")
                                        .anyRequest().authenticated()
                )
                .formLogin(
                        formLogin ->
                                formLogin.loginPage("/users/login")
                                        .usernameParameter("username")
                                        .passwordParameter("password")
                                        .loginProcessingUrl("/users/perform-login")
                                        .defaultSuccessUrl("/")
                                        .failureUrl("/users/login?error=true")
                )
                .logout(
                        logout ->
                                logout.logoutUrl("/users/logout")
                                        .logoutSuccessUrl("/")
                                        .invalidateHttpSession(true)

                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BrDissectingUserDetailService userDetailService (UserRepository userRepository) {
        return new BrDissectingUserDetailService(userRepository);
    }
}
