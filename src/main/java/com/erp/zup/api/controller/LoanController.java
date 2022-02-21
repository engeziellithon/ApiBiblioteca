package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.book.request.BookRequestDTO;
import com.erp.zup.api.dto.loan.request.LoanDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestFilterDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestReturnedDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import com.erp.zup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoanController {

    @Autowired
    private LoanService service;
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;
    @Autowired
    private MapperUtil modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        User user = Optional.ofNullable(userService.findUserByEmail(dto.getUserEmail()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for passed email"));

        Loan entity = service.save(new Loan(user,book,LocalDate.now(),false));

        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(
            @PathVariable Long id,
            @RequestBody LoanRequestReturnedDTO dto) {
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        loan = new Loan(loan.getUser(),loan.getBook(),LocalDate.now(),dto.getReturned());

        service.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanRequestFilterDTO dto,
                              @RequestParam(name = "page", required = false, defaultValue = "0") @Valid  int page,
                              @RequestParam(name = "size", required = false, defaultValue = "20") @Valid int size) {

        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0,size > 0 ? size : 0, Sort.by("id"));

        User user = userService.findUserByEmail(dto.getUserEmail());

        Page<Loan> result = service.find(dto.getIsbn(),user.getId(), pageRequest);
        List<LoanDTO> loans = result
                .getContent()
                .stream()
                .map(entity -> {

                    Book book = entity.getBook();
                    BookRequestDTO bookDTO = modelMapper.map(book, BookRequestDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;

                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}
