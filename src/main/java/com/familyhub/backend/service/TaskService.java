package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.dto.TaskDtos;
import com.familyhub.backend.entity.Task;
import com.familyhub.backend.enums.TaskStatus;
import com.familyhub.backend.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccessService accessService;

    public TaskService(TaskRepository taskRepository, AccessService accessService) {
        this.taskRepository = taskRepository;
        this.accessService = accessService;
    }

    public Page<TaskDtos.TaskResponse> list(Long familyId, Long userId, TaskStatus status, int page, int size) {
        accessService.requireMembership(familyId, userId);
        PageRequest pageable = PageRequest.of(page, size);
        Page<Task> result = status == null
                ? taskRepository.findByFamilyId(familyId, pageable)
                : taskRepository.findByFamilyIdAndStatus(familyId, status, pageable);
        return result.map(this::toResponse);
    }

    @Transactional
    public TaskDtos.TaskResponse create(Long familyId, Long userId, TaskDtos.UpsertTaskRequest request) {
        accessService.requireAdmin(familyId, userId);
        request.assigneeIds().forEach(memberId -> accessService.ensureMemberBelongsToFamily(familyId, memberId));
        Task task = new Task();
        writeTask(task, familyId, userId, request);
        return toResponse(taskRepository.save(task));
    }

    public TaskDtos.TaskResponse detail(Long familyId, Long taskId, Long userId) {
        accessService.requireMembership(familyId, userId);
        return toResponse(requireTask(familyId, taskId));
    }

    public TaskDtos.TaskResponse update(Long familyId, Long taskId, Long userId, TaskDtos.UpsertTaskRequest request) {
        accessService.requireAdmin(familyId, userId);
        request.assigneeIds().forEach(memberId -> accessService.ensureMemberBelongsToFamily(familyId, memberId));
        Task task = requireTask(familyId, taskId);
        writeTask(task, familyId, task.getCreatorId(), request);
        return toResponse(taskRepository.save(task));
    }

    public TaskDtos.TaskResponse updateStatus(Long familyId, Long taskId, Long userId, TaskDtos.UpdateTaskStatusRequest request) {
        accessService.requireMembership(familyId, userId);
        Task task = requireTask(familyId, taskId);
        boolean selfAssigned = task.getAssigneeIds().contains(userId);
        if (!selfAssigned) {
            accessService.requireAdmin(familyId, userId);
        }
        task.setStatus(request.status());
        if (request.status() == TaskStatus.DONE) {
            task.setCompletedAt(Instant.now());
        }
        return toResponse(taskRepository.save(task));
    }

    public void delete(Long familyId, Long taskId, Long userId) {
        accessService.requireAdmin(familyId, userId);
        taskRepository.delete(requireTask(familyId, taskId));
    }

    @Transactional
    public void batch(Long familyId, Long userId, TaskDtos.BatchTaskRequest request) {
        accessService.requireAdmin(familyId, userId);
        if (request.taskIds() == null || request.taskIds().isEmpty()) {
            throw new AppException(400, "任务 ID 不能为空");
        }
        for (Long taskId : request.taskIds()) {
            Task task = requireTask(familyId, taskId);
            switch (request.action()) {
                case "complete" -> {
                    task.setStatus(TaskStatus.DONE);
                    task.setCompletedAt(Instant.now());
                }
                case "delete" -> taskRepository.delete(task);
                case "assign" -> {
                    if (request.assigneeIds() == null || request.assigneeIds().isEmpty()) {
                        throw new AppException(400, "分配成员不能为空");
                    }
                    request.assigneeIds().forEach(memberId -> accessService.ensureMemberBelongsToFamily(familyId, memberId));
                    task.setAssigneeIds(new ArrayList<>(request.assigneeIds()));
                    taskRepository.save(task);
                }
                default -> throw new AppException(400, "不支持的批量操作");
            }
        }
    }

    private Task requireTask(Long familyId, Long taskId) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getFamilyId().equals(familyId))
                .orElseThrow(() -> new AppException(404, "任务不存在"));
    }

    private void writeTask(Task task, Long familyId, Long creatorId, TaskDtos.UpsertTaskRequest request) {
        task.setFamilyId(familyId);
        task.setCreatorId(creatorId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority() == null ? task.getPriority() : request.priority());
        task.setAssigneeIds(new ArrayList<>(request.assigneeIds()));
        task.setDueDate(request.dueDate());
        task.setRepeatRule(request.repeatRule());
        task.setRewardPoints(request.rewardPoints() == null ? 0 : request.rewardPoints());
        task.setTags(request.tags() == null ? new ArrayList<>() : new ArrayList<>(request.tags()));
    }

    private TaskDtos.TaskResponse toResponse(Task task) {
        return new TaskDtos.TaskResponse(
                task.getId(),
                task.getFamilyId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getCreatorId(),
                task.getAssigneeIds(),
                task.getDueDate(),
                task.getRepeatRule(),
                task.getRewardPoints(),
                task.getTags(),
                task.getCompletedAt(),
                task.getCreatedAt()
        );
    }
}
