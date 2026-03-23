package com.familyhub.backend.dto;

import com.familyhub.backend.enums.FamilyRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class FamilyDtos {
    private FamilyDtos() {
    }

    public record CreateFamilyRequest(
            @NotBlank(message = "家庭名称不能为空") @Size(max = 50, message = "家庭名称不能超过 50 字") String name,
            String avatarUrl
    ) {
    }

    public record UpdateFamilyRequest(
            @NotBlank(message = "家庭名称不能为空") @Size(max = 50, message = "家庭名称不能超过 50 字") String name,
            String avatarUrl,
            @Size(max = 500, message = "公告长度不能超过 500 字") String announcement
    ) {
    }

    public record JoinFamilyRequest(@NotBlank(message = "邀请码不能为空") String inviteCode) {
    }

    public record UpdateMemberRoleRequest(FamilyRole role, String alias) {
    }

    public record FamilyResponse(
            Long id,
            String name,
            String avatarUrl,
            String inviteCode,
            String announcement,
            Long ownerId
    ) {
    }

    public record MemberResponse(
            Long memberId,
            Long userId,
            String nickname,
            String avatarUrl,
            FamilyRole role,
            String alias,
            Instant joinedAt
    ) {
    }

    public record MyFamiliesResponse(
            List<FamilyResponse> families
    ) {
    }
}
