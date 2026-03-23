package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.FamilyDtos;
import com.familyhub.backend.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/families")
public class FamilyController extends BaseController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping("/mine")
    public ApiResponse<FamilyDtos.MyFamiliesResponse> myFamilies(Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(familyService.myFamilies(currentUser(authentication).userId()), trace(request));
    }

    @PostMapping
    public ApiResponse<FamilyDtos.FamilyResponse> create(@Valid @RequestBody FamilyDtos.CreateFamilyRequest body,
                                                         Authentication authentication,
                                                         HttpServletRequest request) {
        return ApiResponse.success(familyService.createFamily(currentUser(authentication).userId(), body), trace(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<FamilyDtos.FamilyResponse> detail(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(familyService.getFamily(id, currentUser(authentication).userId()), trace(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FamilyDtos.FamilyResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody FamilyDtos.UpdateFamilyRequest body,
                                                         Authentication authentication,
                                                         HttpServletRequest request) {
        return ApiResponse.success(familyService.updateFamily(id, currentUser(authentication).userId(), body), trace(request));
    }

    @PostMapping("/join")
    public ApiResponse<FamilyDtos.FamilyResponse> join(@Valid @RequestBody FamilyDtos.JoinFamilyRequest body,
                                                       Authentication authentication,
                                                       HttpServletRequest request) {
        return ApiResponse.success(familyService.joinFamily(currentUser(authentication).userId(), body), trace(request));
    }

    @PostMapping("/{id}/invite")
    public ApiResponse<Map<String, String>> refreshInvite(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(Map.of("inviteCode", familyService.refreshInviteCode(id, currentUser(authentication).userId())), trace(request));
    }

    @GetMapping("/{id}/members")
    public ApiResponse<List<FamilyDtos.MemberResponse>> members(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(familyService.listMembers(id, currentUser(authentication).userId()), trace(request));
    }

    @PatchMapping("/{id}/members/{memberId}")
    public ApiResponse<FamilyDtos.MemberResponse> updateMember(@PathVariable Long id,
                                                               @PathVariable Long memberId,
                                                               @RequestBody FamilyDtos.UpdateMemberRoleRequest body,
                                                               Authentication authentication,
                                                               HttpServletRequest request) {
        return ApiResponse.success(familyService.updateMember(id, memberId, currentUser(authentication).userId(), body), trace(request));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ApiResponse<Void> removeMember(@PathVariable Long id, @PathVariable Long memberId, Authentication authentication, HttpServletRequest request) {
        familyService.removeMember(id, memberId, currentUser(authentication).userId());
        return ApiResponse.success(null, trace(request));
    }

    private String trace(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("traceId"));
    }
}
