package com.erp.zup.domain;

import com.erp.zup.api.config.notifiable.NotifiableValidate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends NotifiableValidate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,updatable = false)
    private Long id;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @JsonIgnore
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;


    @PrePersist
    public void prePersist(){
        dateCreated = OffsetDateTime.now();
        lastUpdated = dateCreated;
    }

    @PreUpdate
    public void preUpdate(){
        lastUpdated = OffsetDateTime.now();
    }

}
