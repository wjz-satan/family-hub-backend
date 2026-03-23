package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.dto.CalendarDtos;
import com.familyhub.backend.entity.CalendarEvent;
import com.familyhub.backend.repository.CalendarEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final AccessService accessService;

    public CalendarService(CalendarEventRepository calendarEventRepository, AccessService accessService) {
        this.calendarEventRepository = calendarEventRepository;
        this.accessService = accessService;
    }

    public List<CalendarDtos.EventResponse> list(Long familyId, Long userId, Instant start, Instant end) {
        accessService.requireMembership(familyId, userId);
        return calendarEventRepository.findByFamilyIdAndStartTimeBetweenOrderByStartTimeAsc(familyId, start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    public CalendarDtos.EventResponse create(Long familyId, Long userId, CalendarDtos.UpsertEventRequest request) {
        accessService.requireMembership(familyId, userId);
        CalendarEvent event = new CalendarEvent();
        writeEvent(event, familyId, userId, request);
        return toResponse(calendarEventRepository.save(event));
    }

    public CalendarDtos.EventResponse update(Long familyId, Long eventId, Long userId, CalendarDtos.UpsertEventRequest request) {
        accessService.requireMembership(familyId, userId);
        CalendarEvent event = requireEvent(familyId, eventId);
        writeEvent(event, familyId, event.getCreatorId(), request);
        return toResponse(calendarEventRepository.save(event));
    }

    public void delete(Long familyId, Long eventId, Long userId) {
        accessService.requireMembership(familyId, userId);
        calendarEventRepository.delete(requireEvent(familyId, eventId));
    }

    private CalendarEvent requireEvent(Long familyId, Long eventId) {
        return calendarEventRepository.findById(eventId)
                .filter(event -> event.getFamilyId().equals(familyId))
                .orElseThrow(() -> new AppException(404, "日程不存在"));
    }

    private void writeEvent(CalendarEvent event, Long familyId, Long creatorId, CalendarDtos.UpsertEventRequest request) {
        if (request.endTime().isBefore(request.startTime())) {
            throw new AppException(400, "结束时间不能早于开始时间");
        }
        if (request.participantIds() != null) {
            request.participantIds().forEach(participant -> accessService.ensureMemberBelongsToFamily(familyId, participant));
        }
        event.setFamilyId(familyId);
        event.setCreatorId(creatorId);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setAllDay(request.allDay());
        event.setColor(request.color());
        event.setParticipantIds(request.participantIds() == null ? new ArrayList<>() : new ArrayList<>(request.participantIds()));
        event.setRemindBeforeMinutes(request.remindBeforeMinutes());
        event.setRepeatRule(request.repeatRule());
    }

    private CalendarDtos.EventResponse toResponse(CalendarEvent event) {
        return new CalendarDtos.EventResponse(
                event.getId(),
                event.getFamilyId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.isAllDay(),
                event.getColor(),
                event.getCreatorId(),
                event.getParticipantIds(),
                event.getRemindBeforeMinutes(),
                event.getRepeatRule(),
                event.getCreatedAt()
        );
    }
}
