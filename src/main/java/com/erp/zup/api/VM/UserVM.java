package com.erp.zup.api.VM;

import lombok.*;


import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserVM {

        @NotNull
        @Email
        public String email;
        @NotNull
        public String name;
        @NotNull
        public String password;
        @NotEmpty
        public List<RoleVM> roles = new ArrayList<>();
}


