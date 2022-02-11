package com.erp.zup.service.AuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthService {

    public AuthResponseDTO GenerateToken(String username, List<String> listRoles, String requestURI, String refresh_token) {

        Algorithm algorithm = Algorithm.HMAC256("Secret".getBytes());

        String access_token =
                JWT.create()
                        .withSubject(username)
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(requestURI)
                        .withClaim("roles", listRoles)
                        .sign(algorithm);

        refresh_token = refresh_token != null && !refresh_token.trim().isEmpty()
                ? refresh_token.trim()
                : JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
                .withIssuer(requestURI)
                .sign(algorithm);

        return new AuthResponseDTO(access_token,refresh_token);
    }

    public DecodedJWT DecodedToken(String token) {
        token = token.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("Secret".getBytes())).build();

        return verifier.verify(token);
    }
}
