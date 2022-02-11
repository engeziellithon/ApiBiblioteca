package com.erp.zup.service.user;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.user.request.RoleDTO;
import com.erp.zup.api.dto.user.request.UserDTO;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class UserServiceTest {

    private static final Long ID      = 1L;
    private static final Integer INDEX   = 0;
    private static final String NAME     = "user";
    private static final String EMAIL    = "user@user.com";
    private static final String PASSWORD = "password";
    private static final String ROLE = "User";

    @InjectMocks
    private UserService service;

    @Mock
    private IUserRepository repository;

    @Mock
    private IRoleRepository roleRepo;

    private MapperUtil mapper;

    private User user;
    private UserDTO userVM;
    private Optional<User> optionalUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        start();
    }

    void start() {
        user = new User(ID,NAME, EMAIL, PASSWORD, List.of(new Role(ROLE)));
        userVM = new UserDTO(EMAIL, NAME, PASSWORD, List.of(new RoleDTO(ROLE)));
        optionalUser = Optional.of(new User(ID,NAME, EMAIL, PASSWORD, List.of(new Role(ROLE))));
    }

    @Test
    void whenFindByIdThenReturnAnUserInstance() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);

        User response = service.findById(ID);

        assertNotNull(response);

        assertEquals(User.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(EMAIL, response.getEmail());
    }

    @Test
    void whenFindByIdThenReturnAnObjectNotFoundException() {

        when(repository.findById(anyLong()))
                .thenReturn(null);

        try{
            service.findById(ID);
        } catch (Exception ex) {

        }
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

        User response = service.create(user);

        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(EMAIL, response.getEmail());
        //assertEquals(PASSWORD, response.getPassword());
    }

    @Test
    void whenCreateThenReturnAnDataIntegrityViolationException() {
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(user);

        try{
            optionalUser.get().setId(2L);
            service.create(user);
        } catch (Exception ex) {

        }
    }



    @Test
    void whenUpdateThenReturnAnDataIntegrityViolationException() {
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(user);

        try{
            optionalUser.get().setId(2L);
            service.create(user);
        } catch (Exception ex) {

        }
    }

    @Test
    void deleteWithSuccess() {
        when(repository.findById(anyLong())).thenReturn(optionalUser);
        doNothing().when(repository).deleteById(anyLong());
        service.delete(ID);
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenDeleteThenReturnObjectNotFoundException() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        try {
            service.delete(ID);
        } catch (Exception ex) {

        }
    }

    @Test
    void whenUpdateThenReturnSuccess() {
        when(repository.save(any())).thenReturn(user);
        start();
        User response = service.update(user);

        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(EMAIL, response.getEmail());
    }

}