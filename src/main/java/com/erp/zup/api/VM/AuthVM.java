package com.erp.zup.api.VM;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@RequiredArgsConstructor
@Getter
@Setter
public class AuthVM {
    @NotNull
    @Email(message = "Necessário um email válido")
    public String email;
    @NotNull
    public String password;
}
