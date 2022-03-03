package com.erp.zup.api.controller;

import com.erp.zup.api.config.jwt.CustomAuthorizationFilter;
import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.book.request.BookRequestDTO;
import com.erp.zup.api.dto.book.response.BookPaginationResponseDTO;
import com.erp.zup.api.dto.book.response.BookResponseDTO;
import com.erp.zup.api.dto.loan.response.LoanPaginationResponseDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book", description = "Book crud")
public class BookController {
    
    @Autowired
    private BookService service;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private LoanService loanService;

    private static final Logger log = LoggerFactory.getLogger(CustomAuthorizationFilter.class);
    private static final String ID = "/{id}";



    @PostMapping
    @Operation(summary = "Create a book", responses = {
            @ApiResponse(description = "Successful create", responseCode = "201",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity create(@RequestBody @Valid BookRequestDTO dto){
        log.info(" creating a book for isbn: {} ", dto.getIsbn());
        Book entity = service.save(mapper.map( dto, Book.class ));
        if(entity == null)
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(service.getNotifications());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a book details by id", responses = {
            @ApiResponse(description = "Successful", responseCode = "200",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "NotFound", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity get( @PathVariable Long id ){
        log.info(" obtaining details for book id: {} ", id);
        Optional<Book> book = service.getById(id);
        if (book.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(mapper.map(book.get(), BookResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a book by id", responses = {
            @ApiResponse(description = "No Content success", responseCode = "204",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "NotFound", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity delete(@PathVariable Long id){
        log.info(" deleting book of id: {} ", id);
        Optional<Book> book = service.getById(id);
        if (book.isEmpty())
            return ResponseEntity.notFound().build();

        service.delete(book.get());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a book", responses = {
            @ApiResponse(description = "Successful", responseCode = "200",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "NotFound", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity update(@PathVariable Long id, @RequestBody @Valid BookRequestDTO dto){
        log.info(" updating book of id: {} ", id);
        Optional<Book> book = service.getById(id);
        if (book.isEmpty())
            return ResponseEntity.notFound().build();

        Book Updating = new Book(id, dto.getTitle(), dto.getAuthor(), dto.getIsbn(), book.get().getLoans());
        Book BookUpdated = service.update(Updating);

        if (BookUpdated == null)
            return ResponseEntity.badRequest().body(service.getNotifications());

        return ResponseEntity.ok(mapper.map(BookUpdated, BookResponseDTO.class));
    }

    @GetMapping
    @Operation(summary = "Lists books by params", responses = {
            @ApiResponse(description = "Successful", responseCode = "200",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookPaginationResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity find( BookRequestDTO dto, Pageable pageRequest ){
        Book filter = mapper.map(dto, Book.class);

        Page<Book> list = service.find(filter, pageRequest);

        return ResponseEntity.ok().body(mapper.mapToGenericPagination(list, BookPaginationResponseDTO.class));
    }

    @GetMapping("{id}/loans")
    @Operation(summary = "Lists loans by id book", responses = {
            @ApiResponse(description = "Successful", responseCode = "200",content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanPaginationResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "NotFound", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity loansByBook(@PathVariable Long id,
                                     @Valid @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                     @Valid @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Optional<Book> book = service.getById(id);
        if (book.isEmpty())
            return ResponseEntity.notFound().build();

        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0,size > 0 ? size : 0, Sort.by("id"));

        Page<Loan> list = loanService.getLoansByBook(book.get(), pageRequest);

        return ResponseEntity.ok().body(mapper.mapToGenericPagination(list, LoanPaginationResponseDTO.class));
    }
}
