package com.erp.zup.service.book;

import com.erp.zup.api.config.notifiable.NotifiableValidate;
import com.erp.zup.domain.BaseEntity;
import com.erp.zup.domain.Book;
import com.erp.zup.repository.IBookRepository;
import jflunt.notifications.Notification;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService extends NotifiableValidate implements IBookService {
    private final IBookRepository repository;

    public BookService(IBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            addNotification(new Notification("Isbn","Isbn já cadastrado."));
            return null;
        }

        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(Optional.ofNullable(book).map(BaseEntity::getId).orElse(null) != null)
            this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        Optional<Book> bookCheck = repository.findByIsbn(book.getIsbn());
        if(bookCheck.isPresent() && !bookCheck.get().getId().equals(book.getId())){
            addNotification(new Notification("Isbn","Isbn já cadastrado."));
            return null;
        }

        return this.repository.save(book);
    }

    @Override
    public Page<Book> find( Book filter, Pageable pageRequest ) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
        ) ;
        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }
}
