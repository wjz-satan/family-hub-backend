package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.DashboardDtos;
import com.familyhub.backend.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/families/{familyId}/dashboard")
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ApiResponse<DashboardDtos.DashboardResponse> overview(@PathVariable Long familyId, Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(dashboardService.overview(familyId, currentUser(authentication).userId()), String.valueOf(request.getAttribute("traceId")));
    }
}
