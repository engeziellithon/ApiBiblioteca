package com.erp.zup.api.dto.auth.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Getter
@Setter
public class AuthDTO {
    @NotEmpty(message = "Necessário um email válido")  @NotNull(message = "Necessário um email válido")  @Email(message = "Necessário um email válido")
    public String email;
    @NotEmpty(message = "Senha obrigátoria")   @NotNull(message = "Senha obrigátoria")
    public String password;
}
