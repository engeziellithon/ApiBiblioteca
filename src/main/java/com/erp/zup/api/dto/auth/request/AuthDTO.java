package com.erp.zup.api.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@Getter
@Setter
public class AuthDTO {
    @NotBlank(message = "Necessário um email válido")
    @Email(message = "Necessário um email válido")
    public String email;
    @NotBlank(message = "Senha obrigátoria")
    public String password;
}
