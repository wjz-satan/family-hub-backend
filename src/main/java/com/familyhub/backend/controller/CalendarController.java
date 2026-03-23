package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.CalendarDtos;
import com.familyhub.backend.service.CalendarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/families/{familyId}/events")
public class CalendarController extends BaseController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public ApiResponse<List<CalendarDtos.EventResponse>> list(@PathVariable Long familyId,
                                                              @RequestParam Instant start,
                                                              @RequestParam Instant end,
                                                              Authentication authentication,
                                                              HttpServletRequest request) {
        return ApiResponse.success(calendarService.list(familyId, currentUser(authentication).userId(), start, end), trace(request));
    }

    @PostMapping
    public ApiResponse<CalendarDtos.EventResponse> create(@PathVariable Long familyId,
                                                          @Valid @RequestBody CalendarDtos.UpsertEventRequest body,
                                                          Authentication authentication,
                                                          HttpServletRequest request) {
        return ApiResponse.success(calendarService.create(familyId, currentUser(authentication).userId(), body), trace(request));
    }

    @PutMapping("/{eventId}")
    public ApiResponse<CalendarDtos.EventResponse> update(@PathVariable Long familyId,
                                                          @PathVariable Long eventId,
                                                          @Valid @RequestBody CalendarDtos.UpsertEventRequest body,
                                                          Authentication authentication,
                                                          HttpServletRequest request) {
        return ApiResponse.success(calendarService.update(familyId, eventId, currentUser(authentication).userId(), body), trace(request));
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> delete(@PathVariable Long familyId, @PathVariable Long eventId, Authentication authentication, HttpServletRequest request) {
        calendarService.delete(familyId, eventId, currentUser(authentication).userId());
        return ApiResponse.success(null, trace(request));
    }

    private String trace(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("traceId"));
    }
}
