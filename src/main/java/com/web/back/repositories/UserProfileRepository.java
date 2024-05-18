package com.web.back.repositories;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.User;
import com.web.back.model.entities.UserProfile;
import com.web.back.model.entities.UserProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UserProfileId> {
    void deleteByUser(User user);
    Optional<UserProfile> findByProfile(Profile profile);
}
