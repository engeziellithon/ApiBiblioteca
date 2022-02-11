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
public class UserDTO {
        @NotNull
        @Email
        public String email;
        @NotNull
        public String name;
        @NotNull @JsonProperty(access = WRITE_ONLY)
        public String password;
        @NotEmpty
        public List<RoleDTO> roles;
}


