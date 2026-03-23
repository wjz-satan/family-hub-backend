package com.familyhub.backend.repository;

import com.familyhub.backend.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByFamilyIdAndStartTimeBetweenOrderByStartTimeAsc(Long familyId, Instant start, Instant end);
}
