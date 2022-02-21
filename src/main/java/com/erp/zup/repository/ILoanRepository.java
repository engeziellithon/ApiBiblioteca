package com.erp.zup.repository;

import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@EnableJpaRepositories
public interface ILoanRepository extends JpaRepository<Loan, Long> {

    @Query("select l from Loan l where l.loanDate <= :threeDaysAgo and ( l.returned is null or l.returned is false )")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

    @Query(value = " select l from Loan as l join l.book as b join l.user as u where b.isbn =:isbn or u.id =:id ")
    Page<Loan> findByBookIsbnOrUser(
            @Param("isbn") String isbn,
            @Param("id") Long id,
            Pageable pageable
    );

    Page<Loan> findByBook(Book book, Pageable pageable);


    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end " +
            " from Loan l where l.book = :book and ( l.returned is null or l.returned is false ) ")
    boolean existsByBookAndNotReturned(@Param("book") Book book);
}