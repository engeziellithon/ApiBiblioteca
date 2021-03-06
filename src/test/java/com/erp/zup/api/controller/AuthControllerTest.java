package com.erp.zup.api.controller;

import com.auth0.jwt.JWT;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.auth.AuthService;
import com.erp.zup.service.user.UserService;
import jflunt.notifications.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {
    private static final Long ID         = 1L;
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";
    private static final String ROLE     = "User";
    private static final String Token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    private User user;
    private AuthDTO authDTO;
    private AuthResponseDTO authResponseDTO;

    @InjectMocks
    private AuthController controller;

    @Mock
    private UserService service;

    @Mock
    private AuthService authService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockMvcBuilders.standaloneSetup(controller).build();
        startUser();
    }

    private void startUser() {
        authDTO = new AuthDTO(EMAIL, PASSWORD);
        user = new User(ID,NAME, EMAIL, PASSWORD,List.of(new Role(ID,ROLE)));
        authResponseDTO = new AuthResponseDTO(Token,Token);
    }

    @Test
    void whenAuthWithCorrectDataThenReturnSuccess() {
        user.EncodePassword();
        when(service.findUserByEmail(anyString())).thenReturn(user);
        when(authService.GenerateToken(anyString(), any(),anyString(),anyString())).thenReturn(authResponseDTO);

        ResponseEntity<AuthResponseDTO> response = controller.auth(authDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(AuthResponseDTO.class, response.getBody().getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(Token, response.getBody().accessToken);
        assertEquals(Token, response.getBody().refreshToken);
        assertThat(Token,containsString(""));

        verify(authService, times(1)).GenerateToken(any(),any(),any(),any());
    }

    @Test

    void whenAuthWithIncorrectPasswordThenReturnNotFound() {
        when(service.findUserByEmail(anyString())).thenReturn(null);
        when(authService.GenerateToken(anyString(), any(), any(), any())).thenReturn(authResponseDTO);
        ResponseEntity<List<Notification>> response = controller.auth(authDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals("User", response.getBody().get(0).getProperty());
        assertEquals("Usu??rio n??o encontrado ou senha incorreta", response.getBody().get(0).getMessage());
        verify(authService, times(0)).GenerateToken(any(),any(),any(),any());
    }

    @Test
    void whenSendAnNotExistingEmailThenReturnNotFound()  {
        user.EncodePassword();
        when(service.findUserByEmail(anyString())).thenReturn(null);
        when(authService.GenerateToken(anyString(),any(),any(),any())).thenReturn(authResponseDTO);


        ResponseEntity<List<Notification>> response = controller.auth(authDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals("User", response.getBody().get(0).getProperty());
        assertEquals("Usu??rio n??o encontrado ou senha incorreta", response.getBody().get(0).getMessage());
        verify(authService, times(0)).GenerateToken(any(),any(),any(),any());
    }



    @Test
    void whenCallRefreshTokenThenReturnToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("AUTHORIZATION","Bearer " + Token);

        when(authService.DecodedToken(anyString())).thenReturn(JWT.decode(Token));
        when(service.findUserByEmail(anyString())).thenReturn(user);
        when(authService.GenerateToken(anyString(),any(),any(),any())).thenReturn(authResponseDTO);

        ResponseEntity<AuthResponseDTO> response = controller.refreshToken(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(Token, response.getBody().accessToken);
        assertEquals(Token, response.getBody().refreshToken);
        assertThat(Token,containsString(""));
        verify(authService, times(1)).GenerateToken(any(),any(),any(),any());
    }

    @Test
    void whenCallRefreshTokenThenReturnForbidden() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("AUTHORIZATION","Bearer " + Token);

        when(authService.DecodedToken(anyString())).thenReturn(null);
        when(service.findUserByEmail(anyString())).thenReturn(user);
        when(authService.GenerateToken(anyString(),any(),any(),any())).thenReturn(authResponseDTO);

        ResponseEntity<List<Notification>> response = controller.refreshToken(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode().value());
        assertEquals("AUTHORIZATION", response.getBody().get(0).getProperty());
        assertEquals("Dados de autentica????o incorretos ou o token expirou", response.getBody().get(0).getMessage());
        verify(authService, times(0)).GenerateToken(any(),any(),any(),any());
    }

    @Test
    void whenCallRefreshTokenWithoutTokenReturnForbidden() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(authService.DecodedToken(anyString())).thenReturn(null);
        when(service.findUserByEmail(anyString())).thenReturn(user);
        when(authService.GenerateToken(anyString(),any(),any(),any())).thenReturn(authResponseDTO);

        ResponseEntity<List<Notification>> response = controller.refreshToken(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode().value());
        assertEquals("AUTHORIZATION", response.getBody().get(0).getProperty());
        assertEquals("O token n??o foi enviada no cabe??alho", response.getBody().get(0).getMessage());
        verify(authService, times(0)).GenerateToken(any(),any(),any(),any());
    }
}