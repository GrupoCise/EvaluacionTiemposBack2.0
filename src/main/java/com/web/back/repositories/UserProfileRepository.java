package com.web.back.repositories;

import com.web.back.model.entities.User;
import com.web.back.model.entities.UserProfile;
import com.web.back.model.entities.UserProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UserProfileId> {
    long deleteByUser(User user);
}
