package com.erp.zup.api.dto.loan.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestFilterDTO {
    @NotBlank(message = "Campo obrigatório")
    private String isbn;
    @NotBlank(message = "Campo obrigatório")
    private String userEmail;
}
