package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.TaskDtos;
import com.familyhub.backend.enums.TaskStatus;
import com.familyhub.backend.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/families/{familyId}/tasks")
public class TaskController extends BaseController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ApiResponse<Page<TaskDtos.TaskResponse>> list(@PathVariable Long familyId,
                                                         @RequestParam(required = false) TaskStatus status,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         Authentication authentication,
                                                         HttpServletRequest request) {
        return ApiResponse.success(taskService.list(familyId, currentUser(authentication).userId(), status, page, size), trace(request));
    }

    @PostMapping
    public ApiResponse<TaskDtos.TaskResponse> create(@PathVariable Long familyId,
                                                     @Valid @RequestBody TaskDtos.UpsertTaskRequest body,
                                                     Authentication authentication,
                                                     HttpServletRequest request) {
        return ApiResponse.success(taskService.create(familyId, currentUser(authentication).userId(), body), trace(request));
    }

    @GetMapping("/{taskId}")
    public ApiResponse<TaskDtos.TaskResponse> detail(@PathVariable Long familyId, @PathVariable Long taskId, Authentication authentication, HttpServletRequest request) {
        return ApiResponse.success(taskService.detail(familyId, taskId, currentUser(authentication).userId()), trace(request));
    }

    @PutMapping("/{taskId}")
    public ApiResponse<TaskDtos.TaskResponse> update(@PathVariable Long familyId,
                                                     @PathVariable Long taskId,
                                                     @Valid @RequestBody TaskDtos.UpsertTaskRequest body,
                                                     Authentication authentication,
                                                     HttpServletRequest request) {
        return ApiResponse.success(taskService.update(familyId, taskId, currentUser(authentication).userId(), body), trace(request));
    }

    @PatchMapping("/{taskId}/status")
    public ApiResponse<TaskDtos.TaskResponse> updateStatus(@PathVariable Long familyId,
                                                           @PathVariable Long taskId,
                                                           @Valid @RequestBody TaskDtos.UpdateTaskStatusRequest body,
                                                           Authentication authentication,
                                                           HttpServletRequest request) {
        return ApiResponse.success(taskService.updateStatus(familyId, taskId, currentUser(authentication).userId(), body), trace(request));
    }

    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> delete(@PathVariable Long familyId, @PathVariable Long taskId, Authentication authentication, HttpServletRequest request) {
        taskService.delete(familyId, taskId, currentUser(authentication).userId());
        return ApiResponse.success(null, trace(request));
    }

    @PostMapping("/batch")
    public ApiResponse<Void> batch(@PathVariable Long familyId,
                                   @RequestBody TaskDtos.BatchTaskRequest body,
                                   Authentication authentication,
                                   HttpServletRequest request) {
        taskService.batch(familyId, currentUser(authentication).userId(), body);
        return ApiResponse.success(null, trace(request));
    }

    private String trace(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("traceId"));
    }
}
