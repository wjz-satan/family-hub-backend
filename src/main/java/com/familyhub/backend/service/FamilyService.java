package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.dto.FamilyDtos;
import com.familyhub.backend.entity.Family;
import com.familyhub.backend.entity.FamilyMember;
import com.familyhub.backend.entity.User;
import com.familyhub.backend.enums.FamilyRole;
import com.familyhub.backend.repository.FamilyMemberRepository;
import com.familyhub.backend.repository.FamilyRepository;
import com.familyhub.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class FamilyService {

    private static final String INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final AccessService accessService;

    public FamilyService(FamilyRepository familyRepository,
                         FamilyMemberRepository familyMemberRepository,
                         UserRepository userRepository,
                         AccessService accessService) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    @Transactional
    public FamilyDtos.FamilyResponse createFamily(Long userId, FamilyDtos.CreateFamilyRequest request) {
        Family family = new Family();
        family.setName(request.name());
        family.setAvatarUrl(request.avatarUrl());
        family.setOwnerId(userId);
        family.setInviteCode(generateInviteCode());
        Family saved = familyRepository.save(family);

        FamilyMember member = new FamilyMember();
        member.setFamilyId(saved.getId());
        member.setUserId(userId);
        member.setRole(FamilyRole.SUPER_ADMIN);
        familyMemberRepository.save(member);
        return toFamilyResponse(saved);
    }

    public FamilyDtos.FamilyResponse getFamily(Long familyId, Long userId) {
        accessService.requireMembership(familyId, userId);
        return toFamilyResponse(accessService.requireFamily(familyId));
    }

    public FamilyDtos.MyFamiliesResponse myFamilies(Long userId) {
        List<FamilyDtos.FamilyResponse> families = familyMemberRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(member -> accessService.requireFamily(member.getFamilyId()))
                .map(this::toFamilyResponse)
                .toList();
        return new FamilyDtos.MyFamiliesResponse(families);
    }

    public FamilyDtos.FamilyResponse updateFamily(Long familyId, Long userId, FamilyDtos.UpdateFamilyRequest request) {
        accessService.requireSuperAdmin(familyId, userId);
        Family family = accessService.requireFamily(familyId);
        family.setName(request.name());
        family.setAvatarUrl(request.avatarUrl());
        family.setAnnouncement(request.announcement());
        return toFamilyResponse(familyRepository.save(family));
    }

    @Transactional
    public FamilyDtos.FamilyResponse joinFamily(Long userId, FamilyDtos.JoinFamilyRequest request) {
        Family family = familyRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new AppException(404, "邀请码无效"));
        if (familyMemberRepository.findByFamilyIdAndUserIdAndActiveTrue(family.getId(), userId).isPresent()) {
            throw new AppException(409, "用户已在该家庭中");
        }
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getId());
        member.setUserId(userId);
        member.setRole(FamilyRole.MEMBER);
        familyMemberRepository.save(member);
        return toFamilyResponse(family);
    }

    public String refreshInviteCode(Long familyId, Long userId) {
        accessService.requireSuperAdmin(familyId, userId);
        Family family = accessService.requireFamily(familyId);
        family.setInviteCode(generateInviteCode());
        return familyRepository.save(family).getInviteCode();
    }

    public List<FamilyDtos.MemberResponse> listMembers(Long familyId, Long userId) {
        accessService.requireMembership(familyId, userId);
        return familyMemberRepository.findByFamilyIdAndActiveTrue(familyId).stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId())
                            .orElseThrow(() -> new AppException(404, "用户不存在"));
                    return new FamilyDtos.MemberResponse(
                            member.getId(),
                            user.getId(),
                            user.getNickname(),
                            user.getAvatarUrl(),
                            member.getRole(),
                            member.getAlias(),
                            member.getJoinedAt()
                    );
                })
                .toList();
    }

    public FamilyDtos.MemberResponse updateMember(Long familyId, Long memberId, Long userId, FamilyDtos.UpdateMemberRoleRequest request) {
        accessService.requireAdmin(familyId, userId);
        FamilyMember member = familyMemberRepository.findById(memberId)
                .filter(item -> item.getFamilyId().equals(familyId))
                .orElseThrow(() -> new AppException(404, "成员不存在"));
        if (request.role() != null) {
            member.setRole(request.role());
        }
        if (request.alias() != null) {
            member.setAlias(request.alias());
        }
        FamilyMember saved = familyMemberRepository.save(member);
        User user = accessService.requireUser(saved.getUserId());
        return new FamilyDtos.MemberResponse(saved.getId(), user.getId(), user.getNickname(), user.getAvatarUrl(), saved.getRole(), saved.getAlias(), saved.getJoinedAt());
    }

    public void removeMember(Long familyId, Long memberId, Long userId) {
        accessService.requireAdmin(familyId, userId);
        FamilyMember member = familyMemberRepository.findById(memberId)
                .filter(item -> item.getFamilyId().equals(familyId))
                .orElseThrow(() -> new AppException(404, "成员不存在"));
        member.setActive(false);
        familyMemberRepository.save(member);
    }

    private String generateInviteCode() {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(INVITE_CHARS.charAt(RANDOM.nextInt(INVITE_CHARS.length())));
        }
        return builder.toString();
    }

    private FamilyDtos.FamilyResponse toFamilyResponse(Family family) {
        return new FamilyDtos.FamilyResponse(
                family.getId(),
                family.getName(),
                family.getAvatarUrl(),
                family.getInviteCode(),
                family.getAnnouncement(),
                family.getOwnerId()
        );
    }
}
