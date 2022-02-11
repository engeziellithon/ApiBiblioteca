package com.erp.zup.api.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateRequestDTO {
    @NotNull
    @Email
    public String email;
    @NotNull
    public String name;
    @JsonProperty(access = WRITE_ONLY)
    public String password;
    @NotEmpty
    public List<RoleRequestDTO> roles;
}
