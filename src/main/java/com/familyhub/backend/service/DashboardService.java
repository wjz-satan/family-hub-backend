package com.familyhub.backend.service;

import com.familyhub.backend.dto.DashboardDtos;
import com.familyhub.backend.entity.User;
import com.familyhub.backend.enums.BillType;
import com.familyhub.backend.enums.TaskStatus;
import com.familyhub.backend.repository.BillRepository;
import com.familyhub.backend.repository.CalendarEventRepository;
import com.familyhub.backend.repository.FamilyMemberRepository;
import com.familyhub.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final BillRepository billRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final AccessService accessService;

    public DashboardService(TaskRepository taskRepository,
                            BillRepository billRepository,
                            FamilyMemberRepository familyMemberRepository,
                            CalendarEventRepository calendarEventRepository,
                            AccessService accessService) {
        this.taskRepository = taskRepository;
        this.billRepository = billRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.accessService = accessService;
    }

    public DashboardDtos.DashboardResponse overview(Long familyId, Long userId) {
        accessService.requireMembership(familyId, userId);
        ZoneOffset zone = ZoneOffset.UTC;
        LocalDate today = LocalDate.now(zone);
        Instant startOfDay = today.atStartOfDay().toInstant(zone);
        Instant endOfDay = today.plusDays(1).atStartOfDay().toInstant(zone).minusSeconds(1);
        YearMonth month = YearMonth.from(today);

        long todoCount = taskRepository.countByFamilyIdAndStatusNot(familyId, TaskStatus.DONE);
        BigDecimal monthExpense = billRepository.sumAmount(familyId, BillType.EXPENSE, month.atDay(1), month.atEndOfMonth()).orElse(BigDecimal.ZERO);
        String activeMembers = familyMemberRepository.findByFamilyIdAndActiveTrue(familyId).size() + "/" + familyMemberRepository.findByFamilyIdAndActiveTrue(familyId).size();

        List<DashboardDtos.TrendPoint> taskTrend = java.util.stream.IntStream.rangeClosed(0, 6)
                .mapToObj(offset -> today.minusDays(6L - offset))
                .map(date -> new DashboardDtos.TrendPoint(date.toString(), 0, 0))
                .toList();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(zone);
        List<DashboardDtos.TodayEvent> todayEvents = calendarEventRepository.findByFamilyIdAndStartTimeBetweenOrderByStartTimeAsc(familyId, startOfDay, endOfDay).stream()
                .map(event -> new DashboardDtos.TodayEvent(event.getId(), event.getTitle(), timeFormatter.format(event.getStartTime())))
                .toList();

        List<DashboardDtos.MemberCompletion> memberCompletion = familyMemberRepository.findByFamilyIdAndActiveTrue(familyId).stream()
                .map(member -> {
                    User user = accessService.requireUser(member.getUserId());
                    long total = taskRepository.findByFamilyId(familyId, org.springframework.data.domain.Pageable.unpaged()).stream()
                            .filter(task -> task.getAssigneeIds().contains(member.getUserId()))
                            .count();
                    long completed = taskRepository.findByFamilyIdAndStatus(familyId, TaskStatus.DONE, org.springframework.data.domain.Pageable.unpaged()).stream()
                            .filter(task -> task.getAssigneeIds().contains(member.getUserId()))
                            .count();
                    return new DashboardDtos.MemberCompletion(user.getNickname(), completed, total);
                })
                .toList();

        return new DashboardDtos.DashboardResponse(todoCount, monthExpense, activeMembers, taskTrend, todayEvents, memberCompletion);
    }
}
