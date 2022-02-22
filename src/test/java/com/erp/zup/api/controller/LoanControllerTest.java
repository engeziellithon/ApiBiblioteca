package com.erp.zup.api.controller;

import com.erp.zup.api.dto.loan.request.LoanDTO;
import com.erp.zup.api.dto.loan.request.LoanRequestReturnedDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.service.book.BookService;
import com.erp.zup.service.loan.LoanService;
import com.erp.zup.service.loan.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    public void createLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").userEmail("user@user.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        User user = User.builder().email("user@user.com").build();
        user.setId(1L);

        Book book = Book.builder().isbn("123").build();
        book.setId(1l);
        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn(Optional.of(book) );

        Loan loan = Loan.builder().user(user).book(book).loanDate(LocalDate.now()).build();
        loan.setId(1l);
        BDDMockito.given( loanService.save(any(Loan.class)) ).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isCreated() )
                .andExpect( content().string("1") )
        ;

    }

    @Test
    public void invalidIsbnCreateLoanTest() throws  Exception{

        LoanDTO dto = LoanDTO.builder().isbn("123").userEmail("user@user.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn( Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"))
        ;
    }

    @Test
    public void loanedBookErrorOnCreateLoanTest() throws  Exception{

        LoanDTO dto = LoanDTO.builder().isbn("123").userEmail("user@user.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().isbn("123").build();
        book.setId(1l);
        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn(Optional.of(book) );

        BDDMockito.given( loanService.save(any(Loan.class)) )
                .willThrow(new IllegalArgumentException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value("Book already loaned"))
        ;
    }

    @Test
    public void returnBookTest() throws Exception{
        LoanRequestReturnedDTO dto = LoanRequestReturnedDTO.builder().returned(true).build();
        Loan loan = Loan.builder().build();
        loan.setId(1l);
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect( status().isOk() );

        Mockito.verify(loanService, Mockito.times(1)).update(loan);

    }

    @Test
    public void returnInexistentBookTest() throws Exception{
        
        LoanRequestReturnedDTO dto = LoanRequestReturnedDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect( status().isNotFound() );

    }

    @Test
    public void findLoansTest() throws Exception{
        //cenário
        Long id = 1l;
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().isbn("321").build();
        book.setId(1L);
        loan.setBook(book);

        BDDMockito.given( loanService.find( any(),any(), any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>( Arrays.asList(loan), PageRequest.of(0,10), 1 ) );

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10",
                book.getIsbn(), loan.getUser());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(10) )
                .andExpect( jsonPath("pageable.pageNumber").value(0));
    }

}