package com.erp.zup.service.book;

import com.erp.zup.domain.Book;
import com.erp.zup.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    @MockBean
    BookService service;

    @MockBean
    IBookRepository repository;
    
    private static final Long id = 1L;

    @BeforeEach
    public void setUp(){
        this.service = new BookService(repository);
    }

    private Book createValidBook(String isbn) {
        Book book = new Book("As aventuras", "Fulano",isbn, new ArrayList<>());
        book.setId(1L);
        return book;
    }


    @Test
    public void saveBook() {
        Book book = createValidBook("123");
        when(repository.existsByIsbn(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(book);

        Book savedBook = service.save(book);

        assertNotNull(savedBook.getId());
        assertEquals(savedBook.getIsbn(),"123");
        assertEquals(savedBook.getTitle(),"As aventuras");
        assertEquals(savedBook.getAuthor(),"Fulano");
    }


    @Test
    public void shouldNotSaveABookWithDuplicatedISBN(){
        Book book = createValidBook("123");
        when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        Book response = service.save(book);

        assertNull(response);
        assertEquals(service.isInvalid(),true);
        assertEquals(service.getNotifications().size(),1);
        verify(repository, Mockito.never()).save(book);
    }

    @Test
    public void getById(){
        Book book = createValidBook("123");
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = service.getById(id);
        
        assertTrue(foundBook.isPresent());
        assertEquals( foundBook.get().getId(),id);
        assertEquals( foundBook.get().getAuthor(),book.getAuthor());
        assertEquals( foundBook.get().getIsbn(),book.getIsbn());
        assertEquals( foundBook.get().getTitle(),book.getTitle());
    }

    @Test
    public void bookNotFoundById(){
        when( repository.findById(id) ).thenReturn(Optional.empty());
        Optional<Book> book = service.getById(id);
        assertFalse(book.isPresent());
    }

    @Test
    public void deleteBook(){
        Book book = createValidBook("123");

        service.delete(book);
        assertFalse(service.isInvalid());
        assertTrue(service.getNotifications().size() == 0);
        
        verify(repository, times(1)).delete(book);
    }

    @Test
    public void deleteInvalidBook(){
        Book book = new Book();
        service.delete(book);

        verify( repository, Mockito.never()).delete(book);
    }

    @Test
    public void updateInvalidBook(){
        Book book = createValidBook("123");
        when( repository.findByIsbn(any()) ).thenReturn(Optional.of(book));
        Book response = service.update(new Book());

        assertNull(response);
        assertEquals(service.isInvalid(),true);
        assertEquals(service.getNotifications().size(),1);
        verify(repository, Mockito.never()).save(book);
    }

    @Test
    public void updateBook(){
        Book updatingBook = createValidBook("123");
        updatingBook.setId(id);
        Book updatedBook = createValidBook("123");
        updatedBook.setId(id);
        when( repository.findByIsbn(any()) ).thenReturn(Optional.of(updatedBook));
        when(repository.save(updatingBook)).thenReturn(updatedBook);


        Book book = service.update(updatingBook);

        assertEquals(book.getId(),updatedBook.getId());
        assertEquals(book.getTitle(),updatedBook.getTitle());
        assertEquals(book.getIsbn(),updatedBook.getIsbn());
        assertEquals(book.getAuthor(),updatedBook.getAuthor());
    }

    @Test
    public void findBook(){
        
        Book book = createValidBook("123");

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        
        Page<Book> result = service.find(book, pageRequest);


        
        assertEquals(result.getTotalElements(),1);
        assertEquals(result.getContent(),lista);
        assertEquals(result.getPageable().getPageNumber(),0);
        assertEquals(result.getPageable().getPageSize(),10);
    }

    @Test
    public void getBookByIsbn(){
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn( Optional.of(createValidBook("1230")) );

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertTrue(book.isPresent());
        assertEquals(book.get().getId(),id);
        assertEquals(book.get().getIsbn(),isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }
}