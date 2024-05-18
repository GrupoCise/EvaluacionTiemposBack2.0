package com.web.back.repositories;

import com.web.back.model.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByDescription(String description);
    Optional<Profile> findByDescriptionAndIdIsNot(String description, Integer id);
}
