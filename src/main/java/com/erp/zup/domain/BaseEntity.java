package com.erp.zup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jflunt.notifications.Notifiable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter(value = AccessLevel.PROTECTED)
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
}
