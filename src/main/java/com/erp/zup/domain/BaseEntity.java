package com.erp.zup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jflunt.notifications.Notifiable;

import javax.persistence.*;
import java.time.OffsetDateTime;


@MappedSuperclass
public abstract class BaseEntity extends Notifiable {

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
