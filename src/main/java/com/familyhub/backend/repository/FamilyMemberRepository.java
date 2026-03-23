package com.familyhub.backend.repository;

import com.familyhub.backend.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByFamilyIdAndActiveTrue(Long familyId);
    Optional<FamilyMember> findByFamilyIdAndUserIdAndActiveTrue(Long familyId, Long userId);
    List<FamilyMember> findByUserIdAndActiveTrue(Long userId);
}
