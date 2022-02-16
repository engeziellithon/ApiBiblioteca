package com.erp.zup.service.AuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.erp.zup.api.NotificationValidate;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.IUserService;
import jflunt.notifications.Notification;
import jflunt.validations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthService extends NotificationValidate {

    @Autowired
    private IUserService userService;

    public AuthResponseDTO GenerateToken(String email, List<String> roles, String requestURI, String refresh_token) {

        addNotifications(new Contract()
                .isEmail(email, "Email", "Necessário um email válido.")
                .isTrue(roles != null && !roles.isEmpty(), "Roles", "Necessário informar as funções do usuário."));

        if (isInvalid())
            return null;

        Algorithm algorithm = Algorithm.HMAC256("Secret".getBytes());

        String access_token =
                JWT.create()
                        .withSubject(email)
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(requestURI)
                        .withClaim("roles", roles)
                        .sign(algorithm);

        if (refresh_token == null || refresh_token.trim().isEmpty()) {
            refresh_token = JWT.create().withSubject(email)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
                    .withIssuer(requestURI)
                    .sign(algorithm);
        }

        return new AuthResponseDTO(access_token, refresh_token);
    }



    public String DecodedToken(String token) {
        try {
            addNotifications(new Contract()
                    .isNotNullOrEmpty(token, "Token", "Necessário um token"));

            if (isInvalid())
                return null;

            Algorithm algorithm = Algorithm.HMAC256("Secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT.getSubject();
        } catch (Exception exception) {
            addNotification(new Notification("Token", "Token expirado"));
            return null;
        }
    }
}
