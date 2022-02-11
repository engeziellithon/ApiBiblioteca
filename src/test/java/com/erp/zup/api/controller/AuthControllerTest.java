package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.auth.request.AuthDTO;
import com.erp.zup.api.dto.auth.response.AuthResponseDTO;
import com.erp.zup.api.dto.user.request.RoleDTO;
import com.erp.zup.api.dto.user.request.UserDTO;
import com.erp.zup.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";
    private static final String ROLE = "User";

    private UserDTO userDTO;
    private AuthDTO authDTO;
    private UserDetails userDetails;
    private AuthResponseDTO authResponseDTO;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    private MapperUtil mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        startUser();
    }

    private void startUser() {
        userDTO = new UserDTO(NAME, EMAIL, PASSWORD,List.of(new RoleDTO(ROLE)));
        authDTO = new AuthDTO(EMAIL, PASSWORD);
    }

    @Test
    void whenAuthThenReturnSuccess() throws Exception {

        mockMvc.perform(post("/api/auth")
                        .contentType(APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"user@user.com\",\n" +
                                "  \"password\": \"password\"\n" +
                                "}")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenAuthThenReturnNotFound() {

    }

    @Test
    void refreshToken() {
    }
}