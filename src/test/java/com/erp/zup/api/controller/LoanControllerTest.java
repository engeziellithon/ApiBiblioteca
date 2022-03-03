package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.PaginationDTO;
import com.erp.zup.api.dto.loan.request.LoanDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestFilterDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestReturnedDTO;
import com.erp.zup.api.dto.loan.response.LoanResponseDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import com.erp.zup.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTest {
    @InjectMocks
    private LoanController controller;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanService loanService;

    @Mock
    private MapperUtil mapper;

    private User user;
    private Book book;
    private Loan loan;
    private static final Integer PaginationValue = 1;
    private LoanResponseDTO loanResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockMvcBuilders.standaloneSetup(controller).build();
        start();
    }

    private void start() {

        user = new User(1L,"name","user@user.com","1223",null);
        book = new Book(1l, "teste", "ddd", "123456", null);
        loan = new Loan(user,book,LocalDate.now(),true);
        loanResponseDTO = new LoanResponseDTO(1L,user.getId(),true,LocalDate.now(),null);
        loan.setId(1l);
    }

    @Test
    public void createLoan() {
        LoanDTO dto = LoanDTO.builder().isbn("123456").userEmail("user@user.com").build();

        when(userService.findUserByEmail(any())).thenReturn(user);
        when(bookService.getBookByIsbn("123456")).thenReturn(Optional.of(book));
        when(loanService.save(any(Loan.class))).thenReturn(loan);


        ResponseEntity response = controller.create(dto);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
    }

    @Test
    public void invalidIsbnCreateLoan()   {
        LoanDTO dto = LoanDTO.builder().isbn("123456").userEmail("user@user.com").build();

        when(userService.findUserByEmail(any())).thenReturn(user);
        when(bookService.getBookByIsbn("123456")).thenReturn(Optional.empty());
        when(loanService.save(any(Loan.class))).thenReturn(loan);


        ResponseEntity response = controller.create(dto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void invalidUserCreateLoan()   {
        LoanDTO dto = LoanDTO.builder().isbn("123456").userEmail("user@user.com").build();

        when(userService.findUserByEmail(any())).thenReturn(null);
        when(bookService.getBookByIsbn("123456")).thenReturn(Optional.of(book));
        when(loanService.save(any(Loan.class))).thenReturn(loan);


        ResponseEntity response = controller.create(dto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }


    @Test
    public void returnBook() {
        LoanRequestReturnedDTO dto = LoanRequestReturnedDTO.builder().returned(true).build();
        when(loanService.getById(any())).thenReturn(Optional.of(loan));
        when(loanService.update(any())).thenReturn(loan);
        when(mapper.map(any(), any())).thenReturn(loanResponseDTO);

        ResponseEntity response = controller.returnBook(1L,dto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    public void returnInexistentLoan() {
        LoanRequestReturnedDTO dto = LoanRequestReturnedDTO.builder().returned(true).build();
        when(loanService.getById(any())).thenReturn(Optional.empty());


        ResponseEntity response = controller.returnBook(1L,dto);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }



    @Test
    public void findLoans() {
        LoanRequestFilterDTO dto = LoanRequestFilterDTO.builder().isbn("1233456").userEmail("1233456").build();
        when(loanService.find(any(),any(),Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(loan)));
        when(mapper.mapToGenericPagination(any(), any())).thenReturn(new PaginationDTO<>(PaginationValue, PaginationValue, PaginationValue,PaginationValue, List.of(loan)));

        ResponseEntity<PaginationDTO<LoanResponseDTO>> response = controller.find(dto,1,1);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(PaginationValue, response.getBody().getNumber());
        assertEquals(PaginationValue, response.getBody().getSize());
        assertEquals(PaginationValue, response.getBody().getTotalElements());
        assertEquals(PaginationValue, response.getBody().getTotalPages());
        assertTrue(response.getBody().getContent().size() == 1);
    }





}