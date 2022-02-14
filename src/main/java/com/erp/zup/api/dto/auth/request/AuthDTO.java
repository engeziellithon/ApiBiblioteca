package com.erp.zup.api.dto.auth.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Getter
@Setter
public class AuthDTO {
    @NotNull
    @Email(message = "Necessário um email válido")
    public String email;
    @NotNull
    public String password;
}
