package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.loan.request.LoanDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestFilterDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestReturnedDTO;
import com.erp.zup.api.dto.loan.response.LoanPaginationResponseDTO;
import com.erp.zup.api.dto.loan.response.LoanResponseDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan", description = "Loan")
public class LoanController {

    @Autowired
    private LoanService service;
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;
    @Autowired
    private MapperUtil mapper;

    private static final String ID = "/{id}";

    @PostMapping
    @Operation(summary = "update loan returned", responses = {
            @ApiResponse(description = "Created", responseCode = "201",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "BadRequest not find book or userEmail in database", responseCode = "400", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity create(@RequestBody @Valid LoanDTO dto) {
        Optional<Book> book = bookService.getBookByIsbn(dto.getIsbn());
        if (book.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new Notification("Isbn","Livro não encontrado")));

        User user = userService.findUserByEmail(dto.getUserEmail());
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new Notification("User","Usuário não encontrado")));

        Loan entity = service.save(new Loan(user,book.get(),LocalDate.now(),false));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path(ID).buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("{id}")
    @Operation(summary = "update loan returned", responses = {
            @ApiResponse(description = "Successful", responseCode = "200", content = @Content(schema = @Schema(implementation = LoanResponseDTO.class))),
            @ApiResponse(description = "NotFound loan in database", responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity returnBook(
            @PathVariable @Valid Long id,
            @RequestBody @Valid LoanRequestReturnedDTO dto) {

        Optional<Loan> loan = service.getById(id);
        if (loan.isEmpty())
            return ResponseEntity.notFound().build();

        Loan loanReturn = new Loan(loan.get().getUser(),loan.get().getBook(),LocalDate.now(),dto.getReturned());
        loanReturn.setId(id);
        loanReturn = service.update(loanReturn);

        return ResponseEntity.ok().body(mapper.map(loanReturn, LoanResponseDTO.class));
    }

    @GetMapping
    @Operation(summary = "Lists loans by isbn or email", responses = {
            @ApiResponse(description = "Successful", responseCode = "200", content = @Content(schema = @Schema(implementation = LoanPaginationResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity find(@Valid LoanRequestFilterDTO dto,
                              @RequestParam(name = "page", required = false, defaultValue = "0") @Valid  int page,
                              @RequestParam(name = "size", required = false, defaultValue = "20") @Valid int size) {

        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0,size > 0 ? size : 0, Sort.by("id"));

        Page<Loan> result = service.find(dto.getIsbn(),dto.getUserEmail(), pageRequest);

        return ResponseEntity.ok().body(mapper.mapToGenericPagination(result, LoanPaginationResponseDTO.class));
    }
}
