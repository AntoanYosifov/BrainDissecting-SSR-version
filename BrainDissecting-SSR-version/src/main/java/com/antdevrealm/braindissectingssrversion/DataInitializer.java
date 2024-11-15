package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        List<UserRole> rolesToInitialize = Arrays.asList(UserRole.ADMIN, UserRole.MODERATOR, UserRole.USER);

        rolesToInitialize.forEach(role -> {
            if (roleRepository.findByRole(role).isEmpty()) {
                UserRoleEntity newRole = new UserRoleEntity();
                newRole.setRole(role);
                roleRepository.save(newRole);
                System.out.println("Initialized role: " + role);
            }
        });
    }
}