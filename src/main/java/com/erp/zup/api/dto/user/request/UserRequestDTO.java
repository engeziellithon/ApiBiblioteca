package com.erp.zup.api.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


import javax.validation.constraints.*;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {
        @NotEmpty(message = "Necessário um email válido")
        @NotNull(message = "Necessário um email válido")
        @Email(message = "Necessário um email válido")
        public String email;

        @NotNull(message = "Nome obrigátorio")
        @NotEmpty(message = "Nome obrigátorio")
        public String name;

        @NotNull(message = "Senha obrigátoria")
        @NotEmpty(message = "Senha obrigátoria") @JsonProperty(access = WRITE_ONLY)
        public String password;

        @NotNull(message = "Função obrigátoria")
        @NotEmpty(message = "Função obrigátoria")
        public List<RoleRequestDTO> roles;
}


