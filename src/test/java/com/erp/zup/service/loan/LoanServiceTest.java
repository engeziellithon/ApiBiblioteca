package com.erp.zup.service.loan;


import com.erp.zup.api.dto.loan.request.LoanRequestFilterDTO;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.domain.User;
import com.erp.zup.repository.ILoanRepository;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    ILoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanService(repository);
    }

    @Test
    public void saveLoanTest(){
        Book book = Book.builder().build();
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

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getUser()).isEqualTo(savedLoan.getUser());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    public void loanedBookSaveTest(){
        Book book = Book.builder().build();
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

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);

    }

    @Test
    public void getLoanDetaisTest(){
        //cenário
        Long id = 1l;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when( repository.findById(id) ).thenReturn(Optional.of(loan));

        //execucao
        Optional<Loan> result = service.getById(id);

        //verificacao
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getUser()).isEqualTo(loan.getUser());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify( repository ).findById(id);

    }

    @Test
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when( repository.save(loan) ).thenReturn( loan );

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    public void findLoanTest(){
        //cenario
        LoanRequestFilterDTO loanFilterDTO = LoanRequestFilterDTO.builder().userEmail("user@user.com").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1l);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when( repository.findByBookIsbnOrUser(
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.any(PageRequest.class))
        )
                .thenReturn(page);

        //execucao
        Page<Loan> result = service.find( loanFilterDTO.getIsbn(),1L, pageRequest );


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan(){
        Book book = Book.builder().build();
        book.setId(1L);
        User user = User.builder().email("user@user.com").build();
        user.setId(1L);

        return Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .build();
    }
}