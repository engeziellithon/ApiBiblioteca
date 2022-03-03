package com.erp.zup.service.loan;


import com.erp.zup.api.dto.loan.request.LoanRequestFilterDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.repository.ILoanRepository;
import com.erp.zup.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanService service;

    @MockBean
    ILoanRepository repository;

    @MockBean
    EmailService emailService;

    @BeforeEach
    public void setUp(){
        this.service = new LoanService(repository,emailService);
    }

    @Test
    public void saveLoan(){
        Book book = new Book();
        book.setId(1L);
        User user = User.builder().email("user@user.com").build();
        user.setId(1L);

        Loan savingLoan =
                Loan.builder()
                        .book(book)
                        .user(user)
                        .loanDate(LocalDate.now())
                        .build();

        Loan savedLoan = Loan.builder()
                .loanDate(LocalDate.now())
                .user(user)
                .book(book).build();

        savedLoan.setId(1L);


        when( repository.existsByBookAndNotReturned(book) ).thenReturn(false);
        when( repository.save(savingLoan) ).thenReturn( savedLoan );

        Loan loan = service.save(savingLoan);

        assertEquals(loan.getId(),savedLoan.getId());
        assertEquals(loan.getBook().getId(),savedLoan.getBook().getId());
        assertEquals(loan.getUser(),savedLoan.getUser());
        assertEquals(loan.getLoanDate(),savedLoan.getLoanDate());
    }

    @Test
    public void loanedBookSave(){
        Book book = new Book();
        book.setId(1l);
        User user = User.builder().email("user@user.com").build();
        user.setId(1L);

        Loan savingLoan =
                Loan.builder()
                        .book(book)
                        .user(user)
                        .loanDate(LocalDate.now())
                        .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Loan serviceDB =  service.save(savingLoan);

        assertNull(serviceDB);

        verify(repository, never()).save(savingLoan);
    }

    @Test
    public void getLoanDetails() {
        Long id = 1l;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when( repository.findById(id) ).thenReturn(Optional.of(loan));

     
        Optional<Loan> result = service.getById(id);

      
        assertTrue(result.isPresent());
        assertEquals(result.get().getId(),id);
        assertEquals(result.get().getUser(),loan.getUser());
        assertEquals(result.get().getBook(),loan.getBook());
        assertEquals(result.get().getLoanDate(),loan.getLoanDate());

        verify( repository ).findById(id);

    }

    @Test
    public void updateLoan(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when( repository.save(loan) ).thenReturn( loan );

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    public void findLoan(){
        LoanRequestFilterDTO loanFilterDTO = LoanRequestFilterDTO.builder().userEmail("user@user.com").isbn("321").build();

        Loan loan = createLoan();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when(repository.findByBookIsbnOrUser(Mockito.anyString(),Mockito.anyString(),Mockito.any(PageRequest.class)))
                .thenReturn(page);


        Page<Loan> result = service.find( loanFilterDTO.getIsbn(),"user@user.com", pageRequest );


        assertEquals(1, result.getTotalElements());
        assertEquals(lista, result.getContent());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(10, result.getPageable().getPageSize());
    }

    public static Loan createLoan(){
        Book book = new Book();
        book.setId(1L);
        User user = User.builder().email("user@user.com").build();
        user.setId(1L);

       Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .build();
        loan.setId(1l);

        return loan;
    }
}