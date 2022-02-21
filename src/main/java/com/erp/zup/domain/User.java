package com.erp.zup.domain;

import jflunt.validations.Contract;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(value = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseEntity {

    public User(Long Id, String name, String email, String password, List<Role> roles) {

        addNotifications(new Contract()
                .isEmail(email, "email", "Necessário um email válido")
                .isNotNull(name, "name", "Necessário informar o nome")
                .isTrue(roles != null && !roles.isEmpty(), "roles", "Necessário informar as funções do usuário")
                .hasMinLen(password, 8, "password", "O senha precisa ter no minimo 8 caracteres"));

        this.setId(Id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }


    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();



    public void EncodePassword() {
        setPassword(new BCryptPasswordEncoder().encode(getPassword()));
    }


    public boolean CheckPasswordMatch(String password) {
        return !new BCryptPasswordEncoder().matches(password, getPassword());
    }
}
