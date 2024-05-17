package com.web.back.repositories;

import com.web.back.model.entities.Impersonation;
import com.web.back.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImpersonationRepository extends JpaRepository<Impersonation, Integer> {
    Optional<Impersonation> findByUserAndTargetUser(User user, User targetUser);
    List<Impersonation> findByUser(User user);
}
