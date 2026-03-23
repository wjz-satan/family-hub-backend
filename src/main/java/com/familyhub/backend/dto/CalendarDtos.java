package com.familyhub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class CalendarDtos {
    private CalendarDtos() {
    }

    public record UpsertEventRequest(
            @NotBlank(message = "日程标题不能为空") String title,
            @Size(max = 500, message = "日程描述不能超过 500 字") String description,
            String location,
            @NotNull(message = "开始时间不能为空") Instant startTime,
            @NotNull(message = "结束时间不能为空") Instant endTime,
            boolean allDay,
            String color,
            List<Long> participantIds,
            Integer remindBeforeMinutes,
            String repeatRule
    ) {
    }

    public record EventResponse(
            Long id,
            Long familyId,
            String title,
            String description,
            String location,
            Instant startTime,
            Instant endTime,
            boolean allDay,
            String color,
            Long creatorId,
            List<Long> participantIds,
            Integer remindBeforeMinutes,
            String repeatRule,
            Instant createdAt
    ) {
    }
}
