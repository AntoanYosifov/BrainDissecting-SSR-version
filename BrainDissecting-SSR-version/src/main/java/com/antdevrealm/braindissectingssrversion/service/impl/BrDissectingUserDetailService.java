package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;


public class BrDissectingUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public BrDissectingUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(BrDissectingUserDetailService::map)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found!"));
    }

    private static UserDetails map(UserEntity user) {
            return new BrDissectingUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream().map(r -> r.getRole()).map(BrDissectingUserDetailService::map).toList(),
                    user.getFirstName(),
                    user.getLastName()
            );
    }

    private static GrantedAuthority map(UserRole userRole) {
        return new SimpleGrantedAuthority(
                "ROLE_" + userRole);
    }
}
