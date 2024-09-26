package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;


public class BrDissectingUserDetailService implements UserDetailsService {

    Logger log = LoggerFactory.getLogger(BrDissectingUserDetailService.class);
    private final UserRepository userRepository;

    public BrDissectingUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found!"));

        String roles = userEntity.getRoles().stream().map(r -> r.getRole().toString()).collect(Collectors.joining(", "));

        log.info("User '{}' has roles: {}", username, roles );
        log.info("Banned: '{}'" , userEntity.isBanned());

        UserDetails userDetails = map(userEntity);

        return userDetails;

    }

    private  UserDetails map(UserEntity user) {
        BrDissectingUserDetails brDissectingUserDetails = new BrDissectingUserDetails(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(UserRoleEntity::getRole).map(BrDissectingUserDetailService::map).toList(),
                user.getFirstName(),
                user.getLastName(),
                user.isBanned()
        );

        log.info("BrDissectingUserDetails: {}" , brDissectingUserDetails.isBanned());

        return brDissectingUserDetails;
    }

    private static GrantedAuthority map(UserRole userRole) {
        return new SimpleGrantedAuthority(
                "ROLE_" + userRole);
    }
}
