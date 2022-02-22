package com.erp.zup.api.dto.loan.request;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestFilterDTO {
    @NotBlank(message = "Campo obrigatório")
    private String isbn;
    @NotBlank(message = "Campo obrigatório")
    private String userEmail;
}
