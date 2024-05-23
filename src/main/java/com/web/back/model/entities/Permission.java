package com.web.back.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key_name", nullable = false, length = 30)
    private String keyName;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

}
