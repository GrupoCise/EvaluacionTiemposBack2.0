package com.web.back.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "work_lock")
public class WorkLock {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "area_nomina")
    private String areaNomina;

    @Column(name = "sociedad")
    private String sociedad;

    @Column(name = "locked_on")
    private Instant lockedOn;

    @Column(name = "lock_ends_on")
    private Instant lockEndsOn;

    @Column(name = "lock_starts_on")
    private Instant lockStartsOn;

}
