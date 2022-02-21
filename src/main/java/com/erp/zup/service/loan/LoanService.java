package com.erp.zup.service.loan;

import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.repository.ILoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService implements ILoanService {

    private ILoanRepository repository;

    public LoanService(ILoanRepository repository) {
        this.repository = repository;
    }


    @Override
    public Loan save( Loan loan ) {
        if(repository.existsByBookAndNotReturned(loan.getBook()) ){
            throw new IllegalArgumentException("erro");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Loan> find(String isbn,Long userId, Pageable pageable) {
        return repository.findByBookIsbnOrUser(isbn,userId,pageable);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }
}
