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
public class UserProfileId implements java.io.Serializable {
    private static final long serialVersionUID = 1587407415044189169L;
    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserProfileId entity = (UserProfileId) o;
        return Objects.equals(this.profileId, entity.profileId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, userId);
    }

}
