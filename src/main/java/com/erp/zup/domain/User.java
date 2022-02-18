package com.erp.zup.domain;

import jflunt.validations.Contract;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
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
                .hasMinLen(password, 6, "password", "O senha precisa ter no minimo 6 caracteres"));

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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();


    public void EncodePassword() {
        setPassword(new BCryptPasswordEncoder().encode(getPassword()));
    }


    public boolean CheckPasswordMatch(String password) {
        return !new BCryptPasswordEncoder().matches(password, getPassword());
    }
}
