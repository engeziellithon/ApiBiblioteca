package com.erp.zup.api.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleRequestDTO {
    @NotBlank(message = "Função obrigátoria")
    public String name;
}
