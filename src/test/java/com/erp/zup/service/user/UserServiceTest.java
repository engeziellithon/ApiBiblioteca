package com.erp.zup.service.user;

import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class UserServiceTest {

    private static final Long ID = 1L;
    private static final Integer INDEX = 0;
    private static final String NAME = "user";
    private static final String EMAIL = "user@user.com";
    private static final String PASSWORD = "password";
    private static final Role ROLE = new Role("User");
    private static final List<Role> ROLES = List.of(ROLE);

    @InjectMocks
    private UserService service;

    @Mock
    private IUserRepository repository;

    @Mock
    private IRoleRepository roleRepo;

    private User user;
    private Optional<User> optionalUser;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        start();
    }

    void start() {
        user = new User(ID, NAME, EMAIL, PASSWORD, List.of(ROLE));
        optionalUser = Optional.of(new User(ID, NAME, EMAIL, PASSWORD, List.of(ROLE)));
    }

    @Test
    void whenFindByIdThenReturnAnUser() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);

        Optional<User> response = service.findById(ID);

        assertNotNull(response);
        assertEquals(User.class, response.get().getClass());
        assertEquals(ID, response.get().getId());
        assertEquals(NAME, response.get().getName());
        assertEquals(EMAIL, response.get().getEmail());
        assertEquals(ROLES.stream().map(Role::getName).collect(Collectors.toList()),
                response.get().getRoles().stream().map(Role::getName).collect(Collectors.toList()));
    }

    @Test
    void whenFindByIdThenReturnNotFound() {
        when(repository.findById(anyLong())).thenReturn(null);

        Optional<User> response = service.findById(ID);

        assertEquals(response,Optional.empty());
        assertEquals("User", service.getNotifications().get(0).getProperty());
        assertEquals("Usuário não encontrado.", service.getNotifications().get(0).getMessage());
    }

    @Test
    void whenFindAllThenReturnAnListOfUsers() {
        when(repository.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user)));

        Page<User> response = service.findAll(Pageable.ofSize(1));

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(User.class, response.toList().get(INDEX).getClass());

        assertEquals(ID, response.toList().get(INDEX).getId());
        assertEquals(NAME, response.toList().get(INDEX).getName());
        assertEquals(EMAIL, response.toList().get(INDEX).getEmail());
        assertEquals(PASSWORD, response.toList().get(INDEX).getPassword());
    }

    @Test
    void whenCreateThenReturnSuccess() {
        when(repository.save(any())).thenReturn(user);

        Optional<User> response = service.create(user);

        assertNotNull(response);
        assertEquals(ID, response.get().getId());
        assertEquals(NAME, response.get().getName());
        assertEquals(EMAIL, response.get().getEmail());
    }


    @Test
    void whenCreateThenReturnEmpty() {
        when(service.findUserByEmail(any())).thenReturn(user);

        Optional<User> response = service.create(new User(100L,user.getName(),user.getEmail(),user.getPassword(),user.getRoles()));

        assertEquals(response,Optional.empty());
        assertEquals("User", service.getNotifications().get(0).getProperty());
        assertEquals("Usuário já cadastrado para o email informado.", service.getNotifications().get(0).getMessage());
    }

    @Test
    void whenUpdateThenReturnEmpty() {
        when(service.findUserByEmail(any())).thenReturn(user);

        Optional<User> response = service.update(new User(100L,user.getName(),user.getEmail(),user.getPassword(),user.getRoles()));

        assertEquals(response,Optional.empty());
        assertEquals("User", service.getNotifications().get(0).getProperty());
        assertEquals("Usuário já cadastrado para o email informado.", service.getNotifications().get(0).getMessage());
    }

    @Test
    void deleteWithSuccess() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);
        doNothing().when(repository).deleteById(anyLong());
        service.delete(ID);
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenDeleteThenReturnNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        service.delete(ID);
        assertEquals("User", service.getNotifications().get(0).getProperty());
        assertEquals("Usuário não encontrado.", service.getNotifications().get(0).getMessage());
    }

    @Test
    void whenUpdateThenReturnSuccess() {
        when(repository.save(any())).thenReturn(user);
        when(roleRepo.findByName(anyString())).thenReturn(ROLE);
        Optional<User> response = service.update(user);

        assertNotNull(response);
        assertEquals(User.class, response.get().getClass());
        assertEquals(ID, response.get().getId());
        assertEquals(NAME, response.get().getName());
        assertEquals(EMAIL, response.get().getEmail());
    }

    @Test
    void whenUpdateThenReturnNotFound() {
        when(repository.save(any())).thenReturn(user);

        Optional<User> response = service.update(user);

        assertNotNull(response);
        assertEquals(User.class, response.get().getClass());
        assertEquals(ID, response.get().getId());
        assertEquals(NAME, response.get().getName());
        assertEquals(EMAIL, response.get().getEmail());
    }

    @Test
    void whenCheckUserRegisteredWithSuccess() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);
        when(service.findUserByEmail(anyString())).thenReturn(user);

        service.checkUserRegistered(user);
        assertEquals(0, service.getNotifications().size());
    }

    @Test
    void whenCheckUserRegisteredWithErrorNotFound() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);
        when(service.findUserByEmail(anyString())).thenReturn(user);


        service.checkUserRegistered(new User(null, user.getName(),user.getEmail(),user.getPassword(),user.getRoles()));
        assertEquals("User", service.getNotifications().get(0).getProperty());
        assertEquals("Usuário já cadastrado para o email informado.", service.getNotifications().get(0).getMessage());
    }

    @Test
    void whenCheckUserRegisteredPassNotPassworWithReturnSucess() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);
        when(service.findUserByEmail(anyString())).thenReturn(user);

        service.checkUserRegistered(new User(user.getId(), user.getName(),user.getEmail(),null,user.getRoles()));
        assertEquals(0, service.getNotifications().size());
    }
}