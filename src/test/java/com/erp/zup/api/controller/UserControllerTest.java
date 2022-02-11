package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.user.request.RoleRequestDTO;
import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.api.dto.user.request.UserUpdateRequestDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private static final Long ID         = 1L;
    private static final Integer INDEX   = 0;
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";

    private User user = new User();
    private UserRequestDTO userDTO = new UserRequestDTO();
    private UserUpdateRequestDTO userUpdateRequestDTO = new UserUpdateRequestDTO();

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    private MapperUtil mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    @Test
    void whenFindByIdThenReturnSuccess() {
        when(service.findById(anyLong())).thenReturn(user);
        when(mapper.map(any(), any())).thenReturn(userDTO);

        ResponseEntity<UserRequestDTO> response = controller.findById(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserRequestDTO.class, response.getBody().getClass());

        //assertEquals(ID, response.getBody().getId());
        assertEquals(NAME, response.getBody().getName());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(PASSWORD, response.getBody().getPassword());
    }

    @Test
    void whenFindAllThenReturnAListOfUserDTO() {
        when(service.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user)));
        when(mapper.map(any(), any())).thenReturn(userDTO);

        ResponseEntity<List<UserRequestDTO>> response = controller.findAll(Pageable.ofSize(1));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ArrayList.class, response.getBody().getClass());
        assertEquals(UserRequestDTO.class, response.getBody().get(INDEX).getClass());

        //assertEquals(ID, response.getBody().get(INDEX).getId());
        assertEquals(NAME, response.getBody().get(INDEX).getName());
        assertEquals(EMAIL, response.getBody().get(INDEX).getEmail());
        assertEquals(PASSWORD, response.getBody().get(INDEX).getPassword());
    }

    @Test
    void whenCreateThenReturnCreated() {
        when(service.create(any())).thenReturn(user);

        ResponseEntity<UserRequestDTO> response = controller.create(userDTO);

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Location"));
    }

    @Test
    void whenUpdateThenReturnSuccess() {
        when(service.update(user)).thenReturn(user);
        when(mapper.map(any(), any())).thenReturn(userDTO);

        ResponseEntity<UserRequestDTO> response = controller.update(ID, userUpdateRequestDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserRequestDTO.class, response.getBody().getClass());

        //assertEquals(ID, response.getBody().get);
        assertEquals(NAME, response.getBody().getName());
        assertEquals(EMAIL, response.getBody().getEmail());
    }

    @Test
    void whenDeleteThenReturnSuccess() {
        doNothing().when(service).delete(anyLong());

        ResponseEntity<UserRequestDTO> response = controller.delete(ID);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).delete(anyLong());
    }

    private void startUser() {
        String ROLE = "User";
        user = new User(ID, NAME, EMAIL,  PASSWORD,List.of(new Role(ROLE)));
        userDTO = new UserRequestDTO(NAME, EMAIL, PASSWORD,List.of(new RoleRequestDTO(ROLE)));
        userUpdateRequestDTO = new UserUpdateRequestDTO(NAME, EMAIL, PASSWORD,List.of(new RoleRequestDTO(ROLE)));
        mapper = new MapperUtil();
    }
}