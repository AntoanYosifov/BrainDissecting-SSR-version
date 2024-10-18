package com.antdevrealm.braindissectingssrversion.config;

import com.antdevrealm.braindissectingssrversion.filter.BannedUserFilter;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.impl.ArticleServiceImpl;
import com.antdevrealm.braindissectingssrversion.service.impl.BrDissectingUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

public class SecurityConfig {

    private final static Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, BannedUserFilter bannedUserFilter) throws Exception {

        return httpSecurity
                .addFilterBefore(bannedUserFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .requestMatchers("/", "/users/login", "/users/register",  "/users/banned", "/users/logout")
                                        .permitAll()
                                        .requestMatchers("/admin/**").hasRole("ADMIN")
                                        .requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
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

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, accessDeniedException) -> {
//            if (request.getUserPrincipal() != null) {
//                BrDissectingUserDetails brDissectingUserDetails = (BrDissectingUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();
//
//                if (brDissectingUserDetails.isBanned()) {
//                    response.sendRedirect("/users/banned");
//                    return;
//                }
//            }
//            response.sendRedirect("/access/denied");
//        };
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BrDissectingUserDetailService userDetailService(UserRepository userRepository) {
        return new BrDissectingUserDetailService(userRepository);
    }
}
