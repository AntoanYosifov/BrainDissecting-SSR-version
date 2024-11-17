package com.antdevrealm.braindissectingssrversion.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldForwardToPerformLogin_WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/users/login")
                .param("username", "validUsername")
                .param("password", "validPassword")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/users/perform-login"));
    }
}
