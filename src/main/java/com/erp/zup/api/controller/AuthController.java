package com.erp.zup.api.controller;

import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.AuthService.AuthService;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notifiable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Auth", description = "Authenticate user and update token")
public class AuthController extends Notifiable {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;


    @PostMapping
    public ResponseEntity auth(@Valid @RequestBody AuthDTO auth, HttpServletRequest request) {
        User user = userService.findUserByEmail(auth.getEmail());

        if (user == null || (!new BCryptPasswordEncoder().matches(auth.getPassword(), user.getPassword()))) {
            addNotification("User", "User not found or password incorrect.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getNotifications());
        }

        AuthResponseDTO token = authService.GenerateToken(user.getEmail(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), "/api/auth", "");

        return ResponseEntity.ok(token);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String token = authorizationHeader.substring("Bearer ".length());
            String subject = authService.DecodedToken(authorizationHeader);

            if (subject == null) {
                addNotification("AUTHORIZATION", "Incorrect authorization or refresh token is expired.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getNotifications());
            }

            User user = userService.findUserByEmail(subject);

            AuthResponseDTO tokens = authService.GenerateToken(user.getName(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), request.getRequestURI(), token);

            return ResponseEntity.ok(tokens);
        } else {
            addNotification("AUTHORIZATION", "The authorization not sent in the header.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getNotifications());
        }
    }
}

