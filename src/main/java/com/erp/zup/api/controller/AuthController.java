package com.erp.zup.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.config.jwt.SecurityConfig;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authenticate user and update token")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig security;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public ResponseEntity auth(@Valid @RequestBody AuthDTO auth, HttpServletRequest request, HttpServletResponse response) throws IOException {

       User user = userService.findUserByEmail(auth.getEmail());

       if (user == null || (!passwordEncoder.matches(auth.getPassword(),user.getPassword()))) {
           var message = new HashMap<>() {{
               put("message", "User not found or password incorrect.");
           }};
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
       }

        return ResponseEntity.ok(security.GenerateToken(user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                request, response, null));
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    DecodedJWT decodedJWT = security.DecodedToken(authorizationHeader);

                    User user = userService.findUserByEmail(decodedJWT.getSubject());

                    AuthResponseDTO tokens = security.GenerateToken(user.getName(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), request, response, token);

                    new ObjectMapper().writeValue(response.getOutputStream(),tokens);

                } catch (Exception e) {
                    response.setStatus(FORBIDDEN.value());
                }
            }
            else {
                response.setStatus(FORBIDDEN.value());
                Map<String, String> message = new HashMap<>() {{
                    put("message", "Refresh token is missing");
                }};
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),message);
            }
        }
        catch (Exception ex) {
            response.setStatus(BAD_REQUEST.value());
        }
    }
}
