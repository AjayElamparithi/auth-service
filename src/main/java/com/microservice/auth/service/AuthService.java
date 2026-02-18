package com.microservice.auth.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservice.auth.dto.LoginDTO;
import com.microservice.auth.dto.LoginResponseDTO;
import com.microservice.auth.dto.SignUpRequestDTO;
import com.microservice.auth.entity.Users;
import com.microservice.auth.repository.UsersRepository;
import com.microservice.auth.security.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService appUserDetailService;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private static final List<String> VALID_ROLES = Arrays.asList("USER", "ADMIN", "SELLER");

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public ResponseEntity<?> signup(SignUpRequestDTO request) {
        if (!isValidEmail(request.getUserMail())) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        Users existingUser = usersRepository.findByMailId(request.getUserMail());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists with this email");
        }

        String role = request.getUserRole();
        if (role == null || !VALID_ROLES.contains(role.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid role. Must be one of: USER, ADMIN, SELLER");
        }

        if (request.getLoginPassword() == null || request.getLoginPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        Users newUser = new Users();
        newUser.setUserName(request.getUserName());
        newUser.setUserMail(request.getUserMail());
        newUser.setUserRole(role.toUpperCase());
        newUser.setLoginPassword(passwordEncoder.encode(request.getLoginPassword()));

        usersRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    public ResponseEntity<?> signin(LoginDTO request) {
        if (!isValidEmail(request.getUserMail())) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUserMail(),
                    request.getLoginPassword()
                )
            );

            UserDetails userDetails = appUserDetailService.loadUserByUsername(request.getUserMail());
            String token = jwtUtil.generateToken(userDetails);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    public ResponseEntity<?> validateToken(String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                return ResponseEntity.ok().body("Token is valid");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed");
        }
    }
}
