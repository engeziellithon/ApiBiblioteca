package com.erp.zup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table
public class Book extends BaseEntity {

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    @OneToMany( mappedBy = "book" )
    private List<Loan> loans;
}
