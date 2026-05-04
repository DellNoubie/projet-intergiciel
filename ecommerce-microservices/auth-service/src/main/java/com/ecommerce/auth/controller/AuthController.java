package com.ecommerce.auth.controller;

import com.ecommerce.auth.entity.JwtRequest;
import com.ecommerce.auth.entity.JwtResponse;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.service.CustomUserDetailsService;
import com.ecommerce.auth.service.UserService;
import com.ecommerce.auth.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * POST /authenticate
     * Corps : { "userName": "email@example.com", "userPassword": "motdepasse" }
     * Retourne : { "jwtToken": "...", "username": "..." }
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUserName(),
                            jwtRequest.getUserPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUserName());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername()));
    }

    /**
     * POST /registerNewUser
     * Corps : objet User complet
     * Retourne : l'utilisateur créé (sans le mot de passe)
     */
    @PostMapping("/registerNewUser")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            // Ne jamais retourner le mot de passe dans la réponse
            savedUser.setUserPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /admin/users
     * Liste tous les utilisateurs (Admin uniquement — protégé par @PreAuthorize dans le service)
     */
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
        var users = userService.getAllUsers();
        // Masquer les mots de passe
        users.forEach(u -> u.setUserPassword(null));
        return ResponseEntity.ok(users);
    }
}
