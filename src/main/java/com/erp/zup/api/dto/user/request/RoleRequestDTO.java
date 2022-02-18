package com.erp.zup.api.dto.user.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleRequestDTO {
    @NotNull(message = "Função obrigátoria")
    @NotEmpty(message = "Função obrigátoria")
    public String name;
}
