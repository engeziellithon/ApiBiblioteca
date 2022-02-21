package com.erp.zup.api.dto.loan.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanRequestReturnedDTO {
    @NotNull(message = "Campo obrigatório")
    private Boolean returned;
}
