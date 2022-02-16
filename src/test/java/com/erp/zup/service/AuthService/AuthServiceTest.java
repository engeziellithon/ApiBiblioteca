package com.erp.zup.service.AuthService;

import com.erp.zup.api.controller.AuthController;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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

    @Mock
    private AuthService authServiceMock;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    private void startUser() {
        authDTO = new AuthDTO(EMAIL, PASSWORD);
        user = new User(ID,NAME, EMAIL, PASSWORD, List.of(new Role(ROLE)));
        authResponseDTO = new AuthResponseDTO(Token,Token);
    }

    @Test
    void whenCallGenerateTokenReturnTokenAndRefreshToken() {
        when(authServiceMock.GenerateToken("",new ArrayList<String>(),"",null)).thenReturn(authResponseDTO);

        AuthResponseDTO response = authService.GenerateToken(user.getEmail(),user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),"","");

        assertEquals(AuthResponseDTO.class, response.getClass());
        assertNotNull(response.accessToken);
        assertNotNull(response.refreshToken);
        assertThat(response.accessToken,containsString("."));
        assertThat(response.refreshToken,containsString("."));
        assertEquals(0, authService.getNotifications().size());
    }

    @Test
    void whenCallGenerateTokenReturnNotifications() {
        AuthResponseDTO response = authService.GenerateToken("",null,"","");

        assertNull(response);
        assertEquals("Email", authService.getNotifications().get(0).getProperty());
        assertEquals("Necessário um email válido.", authService.getNotifications().get(0).getMessage());
        assertEquals("Roles", authService.getNotifications().get(1).getProperty());
        assertEquals("Necessário informar as funções do usuário.", authService.getNotifications().get(1).getMessage());
    }

    @Test
    void  whenCallDecodedTokenReturnSubject() {
        AuthResponseDTO authResponse = authService.GenerateToken(user.getEmail(),user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),"","");
        when(authServiceMock.DecodedToken(authResponseDTO.accessToken)).thenReturn(user.getEmail());

        String response = authService.DecodedToken(authResponse.accessToken);

        assertNotNull(response);
        assertEquals(user.getEmail(), response);
        assertEquals(0, authService.getNotifications().size());
    }

    @Test
    void whenCallDecodedTokenReturnNotifications() {
        when(authServiceMock.DecodedToken(null)).thenReturn(null);

        String response = authService.DecodedToken(null);

        assertNull(response);
        assertEquals("Token", authService.getNotifications().get(0).getProperty());
        assertEquals("Necessário um token", authService.getNotifications().get(0).getMessage());
    }


    @Test
    void whenCallDecodedTokenIncorrectTokenParameterReturnNotifications() {
        //when(authServiceMock.DecodedToken(null)).thenReturn(null);
        String response = authService.DecodedToken("teste");

        assertNull(response);
        assertEquals("Token", authService.getNotifications().get(0).getProperty());
        assertEquals("Token expirado", authService.getNotifications().get(0).getMessage());
    }



}