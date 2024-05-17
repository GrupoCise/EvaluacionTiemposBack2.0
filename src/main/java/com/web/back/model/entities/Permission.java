package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key_name", nullable = false, length = 30)
    private String keyName;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

}
