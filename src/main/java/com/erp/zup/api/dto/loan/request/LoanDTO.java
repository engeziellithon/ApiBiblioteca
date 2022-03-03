package com.erp.zup.api.dto.loan.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    @NotBlank(message = "Campo obrigátorio")
    private String isbn;
    @NotBlank(message = "Campo obrigátorio")
    @Email(message = "Necessário um email válido")
    private String userEmail;
}
