package com.ecommerce.auth.service;

import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscription d'un nouvel utilisateur.
     * Le rôle par défaut est "User".
     */
    public User registerUser(User user) {
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cet email : " + user.getUserEmail());
        }

        // Encoder le mot de passe (BCrypt)
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        // Assigner le rôle "User" par défaut
        Role userRole = roleRepository.findByRoleName("User")
                .orElseGet(() -> roleRepository.save(new Role(null, "User")));

        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    /**
     * Retourne tous les utilisateurs (Admin seulement).
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));
    }
}
