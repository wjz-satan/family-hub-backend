package com.familyhub.backend.repository;

import com.familyhub.backend.entity.Task;
import com.familyhub.backend.enums.TaskPriority;
import com.familyhub.backend.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByFamilyId(Long familyId, Pageable pageable);
    Page<Task> findByFamilyIdAndStatus(Long familyId, TaskStatus status, Pageable pageable);
    Page<Task> findByFamilyIdAndPriority(Long familyId, TaskPriority priority, Pageable pageable);
    long countByFamilyIdAndStatusNot(Long familyId, TaskStatus status);
    long countByFamilyIdAndStatus(Long familyId, TaskStatus status);
    List<Task> findTop5ByFamilyIdOrderByCreatedAtDesc(Long familyId);

    @Query("select t from Task t where t.familyId = :familyId and t.dueDate between :start and :end")
    List<Task> findTasksDueBetween(@Param("familyId") Long familyId, @Param("start") Instant start, @Param("end") Instant end);
}
