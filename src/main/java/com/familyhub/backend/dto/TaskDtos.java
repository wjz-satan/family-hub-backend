package com.familyhub.backend.dto;

import com.familyhub.backend.enums.TaskPriority;
import com.familyhub.backend.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class TaskDtos {
    private TaskDtos() {
    }

    public record UpsertTaskRequest(
            @NotBlank(message = "任务标题不能为空") @Size(max = 100, message = "任务标题不能超过 100 字") String title,
            @Size(max = 500, message = "任务描述不能超过 500 字") String description,
            TaskPriority priority,
            @NotEmpty(message = "负责人不能为空") List<Long> assigneeIds,
            Instant dueDate,
            String repeatRule,
            Integer rewardPoints,
            List<String> tags
    ) {
    }

    public record UpdateTaskStatusRequest(TaskStatus status) {
    }

    public record BatchTaskRequest(String action, List<Long> taskIds, List<Long> assigneeIds) {
    }

    public record TaskResponse(
            Long id,
            Long familyId,
            String title,
            String description,
            TaskPriority priority,
            TaskStatus status,
            Long creatorId,
            List<Long> assigneeIds,
            Instant dueDate,
            String repeatRule,
            Integer rewardPoints,
            List<String> tags,
            Instant completedAt,
            Instant createdAt
    ) {
    }
}
