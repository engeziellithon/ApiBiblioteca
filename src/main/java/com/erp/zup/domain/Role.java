package com.erp.zup.domain;

import jflunt.validations.Contract;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(unique=true)
    private String name;

    public Role(String name) {

        addNotifications(new Contract()
                .isNotNull(name, "name", "Necessário informar o nome"));

        this.name = name;
    }


}
