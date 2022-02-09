package com.erp.zup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique=true)
    private String email;
    private String name;
    @JsonIgnore
    private String password;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Role> roles = new ArrayList<>();

    public void EncodePassword(String passwordEncode){
        setPassword(passwordEncode);
    }
}
