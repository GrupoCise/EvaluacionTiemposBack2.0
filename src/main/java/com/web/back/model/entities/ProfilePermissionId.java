package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProfilePermissionId implements java.io.Serializable {
    private static final long serialVersionUID = 3483625995534613790L;
    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @Column(name = "permission_id", nullable = false)
    private Integer permissionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProfilePermissionId entity = (ProfilePermissionId) o;
        return Objects.equals(this.permissionId, entity.permissionId) &&
                Objects.equals(this.profileId, entity.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, profileId);
    }

}
