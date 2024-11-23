package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ModeratorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private void setAuthenticatedUser(String username, Long id, String role ,  boolean isBanned) {
        BrDissectingUserDetails userDetails = new BrDissectingUserDetails(
                id,
                username + "@example.com",
                username,
                "password",
                List.of(() -> "ROLE_" + role),
                "Test",
                "User",
                isBanned
        );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
