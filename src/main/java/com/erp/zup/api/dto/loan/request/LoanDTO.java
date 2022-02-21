package com.erp.zup.api.dto.loan.request;

import com.erp.zup.api.dto.book.request.BookRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    @NotBlank(message = "Campo obrigátorio")
    private String isbn;
    @NotBlank(message = "Campo obrigátorio")
    private String userEmail;
    @NotNull
    private BookRequestDTO book;
}
