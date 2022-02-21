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
        @NotBlank(message = "Necessário um email válido")
        @Email(message = "Necessário um email válido")
        public String email;

        @NotBlank(message = "Nome obrigátorio")
        public String name;

        @NotBlank(message = "Senha obrigátoria")
        @Size(min = 8,message = "A senha deve ter no minimo 8 caracteres")
        @JsonProperty(access = WRITE_ONLY)
        public String password;

        @Size(min = 1,message = "Função obrigátoria")
        public List<RoleRequestDTO> roles;
}


