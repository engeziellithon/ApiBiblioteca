package com.erp.zup.api.dto.loan.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class LoanRequestReturnedDTO {
    @NotNull(message = "Campo obrigatório")
    private Boolean returned;
}
