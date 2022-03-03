package com.erp.zup.service.loan;

import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ILoanService {
    Page<Loan> find(String isbn,String email, Pageable pageable);

    Loan save(Loan loan );

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    //List<Loan> getAllLateLoans();
}
