package com.erp.zup.api.controller;

import com.erp.zup.api.NotificationValidate;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.BaseEntity;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.service.AuthService.AuthService;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notifiable;
import jflunt.notifications.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Auth", description = "Authenticate user and refresh token")
public class AuthController extends Notifiable {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;


    @PostMapping
    public ResponseEntity auth(@RequestBody @Valid AuthDTO auth) {

        User user = userService.findUserByEmail(auth.getEmail());
        if (user == null || user.CheckPasswordMatch(auth.getPassword()))
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(new Notification("User", "Usuário não encontrado ou senha incorreta.")));;

        AuthResponseDTO token = authService.GenerateToken(user.getEmail(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), "/api/auth", "");

        return authService.getNotifications().isEmpty() ?  ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authService.getNotifications());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String token = authorizationHeader.substring("Bearer ".length());
            String subject = authService.DecodedToken(authorizationHeader);

            if (subject == null) {
                addNotification("AUTHORIZATION", "Dados de autenticação incorretos ou o token expirou.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getNotifications());
            }

            User user = userService.findUserByEmail(subject);

            AuthResponseDTO tokens = authService.GenerateToken(user.getName(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), request.getRequestURI(), token);

            return ResponseEntity.ok(tokens);
        } else {
            addNotification("AUTHORIZATION", "O token não foi enviada no cabeçalho.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getNotifications());
        }
    }
}

