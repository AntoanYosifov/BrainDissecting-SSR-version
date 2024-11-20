package com.antdevrealm.braindissectingssrversion.util.factory;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.util.annotation.WithCustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;

public class CustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<SimpleGrantedAuthority> authorities = Arrays.stream(customUser.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE" + role))
                .toList();

        BrDissectingUserDetails userDetails = new BrDissectingUserDetails(customUser.id(),
                customUser.email(),
                customUser.username(),
                "mockPassword",
                authorities,
                "mockFirstname",
                "mockLastname",
                customUser.banned());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "mockPassword", authorities);

        context.setAuthentication(authenticationToken);
        return context;
    }
}
