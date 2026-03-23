package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.entity.Family;
import com.familyhub.backend.entity.FamilyMember;
import com.familyhub.backend.entity.User;
import com.familyhub.backend.enums.FamilyRole;
import com.familyhub.backend.repository.FamilyMemberRepository;
import com.familyhub.backend.repository.FamilyRepository;
import com.familyhub.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    public AccessService(FamilyRepository familyRepository,
                         FamilyMemberRepository familyMemberRepository,
                         UserRepository userRepository) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.userRepository = userRepository;
    }

    public User requireUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(404, "用户不存在"));
    }

    public Family requireFamily(Long familyId) {
        return familyRepository.findById(familyId).orElseThrow(() -> new AppException(404, "家庭不存在"));
    }

    public FamilyMember requireMembership(Long familyId, Long userId) {
        return familyMemberRepository.findByFamilyIdAndUserIdAndActiveTrue(familyId, userId)
                .orElseThrow(() -> new AppException(403, "无权访问该家庭"));
    }

    public void requireAdmin(Long familyId, Long userId) {
        FamilyRole role = requireMembership(familyId, userId).getRole();
        if (role != FamilyRole.SUPER_ADMIN && role != FamilyRole.ADMIN) {
            throw new AppException(403, "需要管理员权限");
        }
    }

    public void requireSuperAdmin(Long familyId, Long userId) {
        if (requireMembership(familyId, userId).getRole() != FamilyRole.SUPER_ADMIN) {
            throw new AppException(403, "需要超级管理员权限");
        }
    }

    public void ensureMemberBelongsToFamily(Long familyId, Long memberUserId) {
        requireMembership(familyId, memberUserId);
    }
}
