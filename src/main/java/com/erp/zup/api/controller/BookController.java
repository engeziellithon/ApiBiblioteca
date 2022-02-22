package com.erp.zup.api.controller;

import com.erp.zup.api.config.jwt.CustomAuthorizationFilter;
import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.book.request.BookRequestDTO;
import com.erp.zup.api.dto.book.response.BookResponseDTO;
import com.erp.zup.api.dto.loan.request.LoanDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
@Tag(name = "Book", description = "Book crud")
public class BookController {
    
    @Autowired
    private BookService service;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private LoanService loanService;

    private static final Logger log = LoggerFactory.getLogger(CustomAuthorizationFilter.class);


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description ="Create a book")
    public ResponseEntity create(@RequestBody @Valid BookRequestDTO dto ){
        log.info(" creating a book for isbn: {} ", dto.getIsbn());
        Book entity = mapper.map( dto, Book.class );
        entity = service.save(entity);

        return ResponseEntity.ok(mapper.map(entity, BookResponseDTO.class));
    }

    @GetMapping("{id}")
    @Operation(description ="Get a book details by id")
    public ResponseEntity get( @PathVariable Long id ){
        log.info(" obtaining details for book id: {} ", id);
        return ResponseEntity.ok(service
                .getById(id)
                .map( book -> mapper.map(book, BookResponseDTO.class)  )
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a book by id")
    public void delete(@PathVariable Long id){
        log.info(" deleting book of id: {} ", id);
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        service.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description ="Updates a book")
    public BookResponseDTO update(@PathVariable Long id, @RequestBody @Valid BookRequestDTO dto){
        log.info(" updating book of id: {} ", id);
        return service.getById(id).map( book -> {

            book = new Book(dto.getTitle(), dto.getAuthor(), book.getIsbn(),book.getLoans());

            book = service.update(book);
            return mapper.map(book, BookResponseDTO.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @GetMapping
    @Operation(description ="Lists books by params")
    public Page<BookResponseDTO> find( BookRequestDTO dto, Pageable pageRequest ){
        Book filter = mapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookResponseDTO> list = result.getContent()
                .stream()
                .map(entity -> mapper.map(entity, BookResponseDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookResponseDTO>( list, pageRequest, result.getTotalElements() );
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id,
                                     @Valid @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                     @Valid @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0,size > 0 ? size : 0, Sort.by("id"));

        Page<Loan> result = loanService.getLoansByBook(book, pageRequest);
        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookRequestDTO bookDTO = mapper.map(loanBook, BookRequestDTO.class);
                    LoanDTO loanDTO = mapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);

                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageRequest, result.getTotalElements());
    }
}
