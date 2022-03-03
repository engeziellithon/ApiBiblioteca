package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.PaginationDTO;
import com.erp.zup.api.dto.user.request.RoleRequestDTO;
import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.api.dto.user.request.UserUpdateRequestDTO;
import com.erp.zup.api.dto.user.response.UserResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    private static final Long ID         = 1L;
    private static final Integer INDEX   = 0;
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";
    private static final Integer PaginationValue = 1;

    private User user;
    private UserRequestDTO userDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private UserResponseDTO userResponseDTO;

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    @Mock
    private MapperUtil mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    private void startUser() {
        String ROLE = "User";

        user = new User(ID, NAME, EMAIL,  PASSWORD, List.of(new Role(ID,ROLE)));
        userDTO = new UserRequestDTO(EMAIL, NAME, PASSWORD,List.of(new RoleRequestDTO(ROLE)));
        userUpdateRequestDTO = new UserUpdateRequestDTO(EMAIL, NAME, PASSWORD,List.of(new RoleRequestDTO(ROLE)));
        userResponseDTO = new UserResponseDTO(EMAIL, NAME,List.of(new RoleRequestDTO(ROLE)));
    }

    @Test
    void whenFindByIdThenReturnSuccess() {
        when(service.findById(any())).thenReturn(Optional.of(user));
        when(mapper.map(any(), any())).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = controller.findById(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserResponseDTO.class, response.getBody().getClass());

        assertEquals(NAME, response.getBody().getName());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(0, service.getNotifications().size());
    }

    @Test
    void whenFindAllThenReturnAListOfUserDTO() {
        when(service.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user)));
        when(mapper.mapToGenericPagination(any(), any())).thenReturn(new PaginationDTO<>(PaginationValue, PaginationValue, PaginationValue,PaginationValue, List.of(userResponseDTO)));


        ResponseEntity<PaginationDTO<UserRequestDTO>> response = controller.findAll(1,1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());


        assertEquals(PaginationValue, response.getBody().getNumber());
        assertEquals(PaginationValue, response.getBody().getSize());
        assertEquals(PaginationValue, response.getBody().getTotalElements());
        assertEquals(PaginationValue, response.getBody().getTotalPages());
        assertEquals(response.getBody().getContent().size(), 1);
        assertEquals(0, service.getNotifications().size());
    }

    @Test
    void whenCreateThenReturnCreated() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(service.create(any())).thenReturn(Optional.of(user));

        ResponseEntity<UserRequestDTO> response = controller.create(userDTO);

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Location"));
    }

    @Test
    void whenUpdateThenReturnSuccess() {
        when(service.update(any())).thenReturn(Optional.of(user));
        when(mapper.map(any(), any())).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = controller.update(ID, userUpdateRequestDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserResponseDTO.class, response.getBody().getClass());
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
}