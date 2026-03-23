package com.familyhub.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public final class DashboardDtos {
    private DashboardDtos() {
    }

    public record DashboardResponse(
            long todayTodoCount,
            BigDecimal currentMonthExpense,
            String activeMembers,
            List<TrendPoint> taskTrend,
            List<TodayEvent> todayEvents,
            List<MemberCompletion> memberCompletion
    ) {
    }

    public record TrendPoint(String date, long created, long completed) {
    }

    public record TodayEvent(Long id, String title, String startTime) {
    }

    public record MemberCompletion(String memberName, long completed, long total) {
    }
}
