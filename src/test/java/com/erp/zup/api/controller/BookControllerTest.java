package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.PaginationDTO;
import com.erp.zup.api.dto.book.request.BookRequestDTO;
import com.erp.zup.api.dto.book.response.BookResponseDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @InjectMocks
    private BookController controller;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanService loanService;

    @Mock
    private MapperUtil mapper;

    private static final Long ID = 1L;
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String ISBN = "isbn";
    
    private User user;
    private Book book;
    private Loan loan;
    private BookRequestDTO bookRequestDTO;
    private BookResponseDTO bookResponseDTO;
    private static final Integer PaginationValue = 1;

    private void start() {
        user = new User(ID,"name","user@user.com","1223",null);
        book = new Book(ID, TITLE,AUTHOR,ISBN, null);
        loan = new Loan(user,book, LocalDate.now(),true);
        bookRequestDTO = new BookRequestDTO(TITLE,AUTHOR,ISBN);
        bookResponseDTO = new BookResponseDTO(ID,TITLE,AUTHOR,ISBN);
        loan.setId(ID);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockMvcBuilders.standaloneSetup(controller).build();
        start();
    }

    @Test
    public void createBook() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Book book = createNewBook();
        when(mapper.map(any(),any())).thenReturn(book);
        when(bookService.getBookByIsbn(any())).thenReturn(Optional.of(book));
        when(userService.findUserByEmail(any())).thenReturn(user);
        when(bookService.save(any())).thenReturn(book);

        ResponseEntity response = controller.create(bookRequestDTO);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
    }

    @Test
    public void createInvalidBook() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Book book = createNewBook();
        when(mapper.map(any(),any())).thenReturn(book);
        when(bookService.save(any())).thenReturn(null);

        ResponseEntity response = controller.create(bookRequestDTO);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void getBookDetails() {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.of(book));
        when(mapper.map(any(),any())).thenReturn(bookResponseDTO);

        ResponseEntity<BookResponseDTO> response = controller.get(book.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(book.getId(), response.getBody().getId());
        assertEquals(book.getTitle(), response.getBody().getTitle());
        assertEquals(book.getAuthor(), response.getBody().getAuthor());
        assertEquals(book.getIsbn(), response.getBody().getIsbn());
    }

    @Test
    public void bookNotFound()  {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.empty());
        when(mapper.map(any(),any())).thenReturn(bookResponseDTO);

        ResponseEntity response = controller.get(book.getId());

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    public void deleteBook()  {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.of(book));

        ResponseEntity response = controller.delete(book.getId());

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
    }

    @Test
    public void deleteNonExistentBook()  {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.empty());

        ResponseEntity response = controller.delete(book.getId());

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    public void updateBook()  {
        when(bookService.getById(any())).thenReturn(Optional.of(book));
        when(bookService.update(any())).thenReturn(book);
        when(mapper.map(any(),any())).thenReturn(bookResponseDTO);

        ResponseEntity<BookResponseDTO> response = controller.update(ID,bookRequestDTO);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(book.getId(), response.getBody().getId());
        assertEquals(book.getIsbn(), response.getBody().getIsbn());
        assertEquals(book.getTitle(), response.getBody().getTitle());
        assertEquals(book.getAuthor(), response.getBody().getAuthor());
    }

    @Test
    public void updateNonExistentBook()  {
        when(bookService.getById(any())).thenReturn(Optional.empty());

        ResponseEntity response = controller.update(ID,bookRequestDTO);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    public void updateBadRequestBook()  {
        when(bookService.getById(any())).thenReturn(Optional.of(book));
        when(bookService.update(any())).thenReturn(null);
        when(mapper.map(any(),any())).thenReturn(bookResponseDTO);

        ResponseEntity response = controller.update(ID,bookRequestDTO);


        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void findBooks() {
        Book book = createNewBook();
        when(mapper.map(any(),any())).thenReturn(book);
        when(bookService.find(any(),any())).thenReturn(new PageImpl<>(List.of(book)));
        when(mapper.mapToGenericPagination(any(), any())).thenReturn(new PaginationDTO<>(PaginationValue, PaginationValue, PaginationValue,PaginationValue, List.of(book)));

        ResponseEntity<PaginationDTO<BookResponseDTO>> response = controller.find(Mockito.any(BookRequestDTO.class),Mockito.any(Pageable.class));


        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(PaginationValue, response.getBody().getNumber());
        assertEquals(PaginationValue, response.getBody().getSize());
        assertEquals(PaginationValue, response.getBody().getTotalPages());
        assertEquals(PaginationValue, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().size() == 1);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    public void loansByBook() {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.of(book));
        when(loanService.getLoansByBook(any(),any())).thenReturn(new PageImpl<>(List.of(loan)));
        when(mapper.mapToGenericPagination(any(), any())).thenReturn(new PaginationDTO<>(PaginationValue, PaginationValue, PaginationValue,PaginationValue, List.of(loan)));

        ResponseEntity<PaginationDTO<LoanResponseDTO>> response = controller.loansByBook(book.getId(),1,1);


        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(PaginationValue, response.getBody().getNumber());
        assertEquals(PaginationValue, response.getBody().getSize());
        assertEquals(PaginationValue, response.getBody().getTotalPages());
        assertEquals(PaginationValue, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().size() == 1);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    public void loansByBookNotFound() {
        Book book = createNewBook();
        when(bookService.getById(any())).thenReturn(Optional.empty());

        ResponseEntity response = controller.loansByBook(book.getId(),1,1);


        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    private Book createNewBook() {
        Book book = new Book(TITLE,AUTHOR,ISBN, new ArrayList<>());
        book.setId(ID);
        return book;
    }
    private Book updateNewBook(String title,String author,String isbn) {
        Book book = new Book(title,author,isbn, new ArrayList<>());
        book.setId(ID);
        return book;
    }
}