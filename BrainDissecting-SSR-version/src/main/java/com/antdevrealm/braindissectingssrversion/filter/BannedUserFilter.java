package com.antdevrealm.braindissectingssrversion.filter;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BannedUserFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/users/banned") || requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || requestURI.startsWith("/images/")) {
            filterChain.doFilter(request, response);
            SecurityContextHolder.clearContext();
            return;
        }

        if(authentication != null && authentication.getPrincipal() instanceof BrDissectingUserDetails brDissectingUserDetails) {
            if(brDissectingUserDetails.isBanned()) {
                response.sendRedirect("/users/banned");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
