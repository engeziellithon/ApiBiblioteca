package com.erp.zup.service.book;

import com.erp.zup.domain.Book;
import com.erp.zup.repository.IBookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    @InjectMocks
    BookService service;

    @MockBean
    IBookRepository repository;

    private Book createValidBook(String title,String author,String isbn) {
        Book book = new Book(title,author,isbn, new ArrayList<>());
        book.setId(1L);
        return book;
    }


    @Test
    public void saveBookTest() {
        //cenario
        Book book = createValidBook("As aventuras","Fulano","123");
        when(repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
        when(repository.save(book)).thenReturn(createValidBook("As aventuras","Fulano","123"));

        //execucao
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }


    @Test
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook("As aventuras","Fulano","123");
        when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificacoes
        assertThat(exception)
                .isInstanceOf(null)
                .hasMessage("Isbn já cadastrado.");

        verify(repository, Mockito.never()).save(book);

    }

    @Test
    public void getByIdTest(){
        Long id = 1l;
        Book book = createValidBook("As aventuras","Fulano","123");
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacoes
        assertThat( foundBook.isPresent() ).isTrue();
        assertThat( foundBook.get().getId()).isEqualTo(id);
        assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    public void bookNotFoundByIdTest(){
        Long id = 1l;
        when( repository.findById(id) ).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        //verificacoes
        assertThat( book.isPresent() ).isFalse();

    }

    @Test
    public void deleteBookTest(){
        Book book = createValidBook("As aventuras","Fulano","123");

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book) );

        //verificacoes
        verify(repository, times(1)).delete(book);
    }

    @Test
    public void deleteInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        verify( repository, Mockito.never() ).delete(book);
    }

    @Test
    public void updateInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        verify( repository, Mockito.never() ).save(book);
    }

    @Test
    public void updateBookTest(){
        //cenário
        long id = 1l;

        //livro a atualizar
        Book updatingBook = new Book();

        //simulacao
        Book updatedBook = createValidBook("As aventuras","Fulano","123");
        updatedBook.setId(id);
        when(repository.save(updatingBook)).thenReturn(updatedBook);

        //exeucao
        Book book = service.update(updatingBook);

        //verificacoes
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    public void findBookTest(){
        //cenario
        Book book = createValidBook("As aventuras","Fulano","123");

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    public void getBookByIsbnTest(){
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn( Optional.of(createValidBook("As aventuras","Fulano","123")) );

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1l);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }
}