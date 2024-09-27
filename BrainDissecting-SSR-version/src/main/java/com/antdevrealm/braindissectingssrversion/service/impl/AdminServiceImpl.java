package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.AdminService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final BrDissectingUserDetailService brDissectingUserDetailService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(BrDissectingUserDetailService brDissectingUserDetailService, UserService userService, UserRepository userRepository, RoleRepository roleRepository) {
        this.brDissectingUserDetailService = brDissectingUserDetailService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean promoteToModerator(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();

        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        if (optModeratorRole.isEmpty()) {
            return false;
        }

        UserRoleEntity moderatorRole = optModeratorRole.get();

        List<UserRoleEntity> userRoles = userEntity.getRoles();

        if (userRoles.contains(moderatorRole)) {
            return false;
        }

        userRoles.add(moderatorRole);
        userRepository.save(userEntity);

        userService.reloadUserDetails(userEntity.getUsername());
        return true;
    }

    @Override
    public boolean demoteFromModerator(Long userId) {

        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();

        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        if (optModeratorRole.isEmpty()) {
            return false;
        }

        UserRoleEntity moderatorRole = optModeratorRole.get();

        List<UserRoleEntity> userRoles = userEntity.getRoles();

        if (!userRoles.contains(moderatorRole)) {
            return false;
        }

        userRoles.remove(moderatorRole);
        userRepository.save(userEntity);

        userService.reloadUserDetails(userEntity.getUsername());
        return true;

    }

    @Override
    public void banUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userEntity.setUserStatus(UserStatus.BANNED);
        userRepository.save(userEntity);
    }

    @Override
    public void removeBan(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userEntity.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
    }

}
