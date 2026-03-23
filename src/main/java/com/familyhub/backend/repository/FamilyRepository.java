package com.familyhub.backend.repository;

import com.familyhub.backend.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findByInviteCode(String inviteCode);
}
