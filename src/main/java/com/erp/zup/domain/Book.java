package com.erp.zup.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table
public class Book extends BaseEntity {

    public Book(Long id, String title, String author, String isbn, List<Loan> loans) {
        this.setId(id);
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.loans = loans;
    }

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    @OneToMany( mappedBy = "book" )
    private List<Loan> loans;
}
