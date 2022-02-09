package com.erp.zup.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.VM.AuthVM;
import com.erp.zup.api.config.jwt.SecurityConfig;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig security;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public Object auth(@Valid @RequestBody AuthVM auth, HttpServletRequest request, HttpServletResponse response) throws IOException {

       UserDetails user = userService.AuthUserByEmail(auth.getEmail());

       if (user == null || (user != null && !passwordEncoder.matches(auth.getPassword(),user.getPassword()))){
           Map<String, String> message = new HashMap<>() {{
               put("message", "User not found or password incorrect.");
           }};
           return message;
       }

        Map<String, String> tokens = security.GenerateToken(user.getUsername(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()), request, response, null);

        return tokens;
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    DecodedJWT decodedJWT = security.DecodedToken(authorizationHeader);

                    User user = userService.GetUsersByEmail(decodedJWT.getSubject());

                    Map<String, String> tokens = security.GenerateToken(user.getName(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), request, response, token);

                    new ObjectMapper().writeValue(response.getOutputStream(),tokens);

                } catch (Exception e) {
                    response.setStatus(FORBIDDEN.value());
                }
            } else {
                response.setStatus(FORBIDDEN.value());
                Map<String, String> message = new HashMap<>() {{
                    put("message", "Refresh token is missing");
                }};
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),message);
            }
        }
        catch (Exception ex) {
            response.setStatus(FORBIDDEN.value());
        }
    }
}
