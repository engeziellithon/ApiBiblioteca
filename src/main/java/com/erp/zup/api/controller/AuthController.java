package com.erp.zup.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.auth.AuthService;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Auth", description = "Authenticate user and refresh token")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;


    @Operation(summary = "Get all user by filter", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
    })
    @PostMapping
    public ResponseEntity auth(@RequestBody @Valid AuthDTO auth) {
        User user = userService.findUserByEmail(auth.getEmail());
        if (user == null || user.CheckPasswordMatch(auth.getPassword()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(new Notification("User", "Usu??rio n??o encontrado ou senha incorreta")));

        AuthResponseDTO token = authService.GenerateToken(user.getEmail(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), "/api/auth", "");

        return authService.getNotifications().isEmpty() ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authService.getNotifications());
    }

    @Operation(summary = "Get all user by filter", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of(new Notification("AUTHORIZATION", "O token n??o foi enviada no cabe??alho")));

        String token = authorizationHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = authService.DecodedToken(token);
        User user = userService.findUserByEmail(Optional.ofNullable(decodedJWT).map(i->i.getSubject()).orElse(null));

        if (user == null || authService.isInvalid())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of(new Notification("AUTHORIZATION", "Dados de autentica????o incorretos ou o token expirou")));

        AuthResponseDTO tokens = authService.GenerateToken(user.getName(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), request.getRequestURI(), token);

        return ResponseEntity.ok(tokens);
    }
}

