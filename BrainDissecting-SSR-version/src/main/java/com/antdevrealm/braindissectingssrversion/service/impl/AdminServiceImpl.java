package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.RoleNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.UserNotFoundException;
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

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl( UserService userService, UserRepository userRepository, RoleRepository roleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void promoteToModerator(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserRoleEntity moderatorRole = roleRepository.findByRole(UserRole.MODERATOR)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.MODERATOR));

        List<UserRoleEntity> userRoles = userEntity.getRoles();

        if (userRoles.contains(moderatorRole)) {
            return;
        }

        userRoles.add(moderatorRole);
        userRepository.save(userEntity);

        userService.reloadUserDetails(userEntity.getUsername());

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
    public boolean banUser(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);

        if(optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();

        if(userEntity.isBanned()) {
            return false;
        }

        userEntity.setStatus(UserStatus.BANNED);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean removeBan(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);

        if(optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();

        if (!userEntity.isBanned()) {
            return false;
        }

        userEntity.setStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
        return true;
    }

}
