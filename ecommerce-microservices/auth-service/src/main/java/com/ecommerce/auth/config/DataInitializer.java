package com.ecommerce.auth.config;

import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
        System.out.println("✅ DataInitializer — Rôles et compte Admin créés avec succès !");
    }

    // ─── Créer les rôles s'ils n'existent pas ─────────────────────

    private void initRoles() {
        if (roleRepository.findByRoleName("Admin").isEmpty()) {
            roleRepository.save(new Role(null, "Admin"));
            System.out.println("  → Rôle 'Admin' créé");
        }
        if (roleRepository.findByRoleName("User").isEmpty()) {
            roleRepository.save(new Role(null, "User"));
            System.out.println("  → Rôle 'User' créé");
        }
    }

    // ─── Créer le compte Admin s'il n'existe pas ──────────────────

    private void initAdminUser() {
        String adminEmail = "admin@ecommerce.com";

        if (userRepository.findByUserEmail(adminEmail).isEmpty()) {

            Role adminRole = roleRepository.findByRoleName("Admin")
                    .orElseThrow(() -> new RuntimeException("Rôle Admin introuvable"));

            Role userRole = roleRepository.findByRoleName("User")
                    .orElseThrow(() -> new RuntimeException("Rôle User introuvable"));

            User admin = new User();
            admin.setUserFirstName("Super");
            admin.setUserLastName("Admin");
            admin.setUserEmail(adminEmail);
            // ✅ Mot de passe encodé en BCrypt — jamais en clair !
            admin.setUserPassword(passwordEncoder.encode("Admin@1234"));
            admin.setUserAddress("Siège Social, 1 rue du Commerce");
            // L'admin a LES DEUX rôles : il peut tout faire
            admin.setRoles(Set.of(adminRole, userRole));

            userRepository.save(admin);

            System.out.println("  → Compte Admin créé :");
            System.out.println("     Email    : " + adminEmail);
            System.out.println("     Password : Admin@1234");
            System.out.println("     Rôles    : Admin + User");
        } else {
            System.out.println("  → Compte Admin déjà existant, skip.");
        }
    }
}
