package com.erp.zup.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.api.config.jwt.notifiable.NotifiableValidate;
import jflunt.notifications.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthService extends NotifiableValidate {

    Logger logger = LogManager.getLogger();

    public AuthResponseDTO GenerateToken(String email, List<String> roles, String requestURI, String refresh_token) {
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



    public DecodedJWT DecodedToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("Secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT;
        } catch (Exception exception) {
            logger.warn("Token expirado!" + exception.getMessage());
            addNotification(new Notification("Token", "Token expirado"));
            return null;
        }
    }
}
