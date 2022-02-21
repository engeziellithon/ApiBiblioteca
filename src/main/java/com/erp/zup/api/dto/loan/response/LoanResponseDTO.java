package com.erp.zup.api.dto.loan.response;

import com.erp.zup.api.dto.book.request.BookRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private Long Id;
    private String isbn;
    private Long userId;
    private BookRequestDTO book;
}
