package com.erp.zup.api.dto.loan.response;

import com.erp.zup.api.dto.book.response.BookResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private Long Id;
    private Long userId;
    private Boolean returned;
    private LocalDate loanDate;
    private BookResponseDTO book;
}
