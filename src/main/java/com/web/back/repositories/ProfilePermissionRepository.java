package com.web.back.repositories;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.ProfilePermission;
import com.web.back.model.entities.ProfilePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, ProfilePermissionId> {
    void deleteByProfile(Profile profile);

    List<ProfilePermission> getByProfile(Profile profile);
}
