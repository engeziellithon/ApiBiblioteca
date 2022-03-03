package com.erp.zup.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import jflunt.notifications.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private static final Long ID         = 1L;
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";
    private static final String ROLE     = "User";
    private static final String Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQHVzZXIuY29tIiwicm9sZXMiOlsidGVzdGUiXSwiaXNzIjoiL2FwaS9hdXRoIiwiZXhwIjoxNjQ0OTY4MTc4fQ._XqO_Dc7NLqAOjrKSUT0qw3wU8sLcJQGaovOMEGuBkI";

    private User user;
    private AuthDTO authDTO;
    private AuthResponseDTO authResponseDTO;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    private void startUser() {
        authDTO = new AuthDTO(EMAIL, PASSWORD);
        user = new User(ID,NAME, EMAIL, PASSWORD, List.of(new Role(ID,ROLE)));
        authResponseDTO = new AuthResponseDTO(Token,Token);
    }

    @Test
    void whenCallGenerateTokenReturnTokenAndRefreshToken() {
        AuthResponseDTO response = authService.GenerateToken(user.getEmail(),user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),"","");

        assertEquals(AuthResponseDTO.class, response.getClass());
        assertNotNull(response.accessToken);
        assertNotNull(response.refreshToken);
        assertThat(response.accessToken,containsString("."));
        assertThat(response.refreshToken,containsString("."));
        assertEquals(0, authService.getNotifications().size());
    }

    @Test
    void  whenCallDecodedTokenReturnSubject() {
        AuthResponseDTO authResponse = authService.GenerateToken(user.getEmail(),user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),"","");
        //when(authServiceMock.DecodedToken(authResponseDTO.accessToken)).thenReturn(JWT.decode(Token));

        DecodedJWT response = authService.DecodedToken(authResponse.accessToken);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getSubject());
        assertEquals(0, authService.getNotifications().size());
    }

    @Test
    void whenCallDecodedTokenIncorrectTokenParameterReturnNotifications() {
        DecodedJWT response = authService.DecodedToken(Token);

        List<Notification> notifications = authService.getNotifications();

        assertNull(response);
        assertEquals("Token", notifications.get(0).getProperty());
        assertEquals("Token expirado", notifications.get(0).getMessage());
    }
}