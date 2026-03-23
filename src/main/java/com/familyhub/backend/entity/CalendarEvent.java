package com.familyhub.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendar_events")
public class CalendarEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long familyId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 200)
    private String location;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private boolean allDay;

    @Column(length = 7)
    private String color;

    @Column(nullable = false)
    private Long creatorId;

    @ElementCollection
    @CollectionTable(name = "calendar_event_participants", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id")
    private List<Long> participantIds = new ArrayList<>();

    private Integer remindBeforeMinutes;

    @Column(length = 100)
    private String repeatRule;

    public Long getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public Integer getRemindBeforeMinutes() {
        return remindBeforeMinutes;
    }

    public void setRemindBeforeMinutes(Integer remindBeforeMinutes) {
        this.remindBeforeMinutes = remindBeforeMinutes;
    }

    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(String repeatRule) {
        this.repeatRule = repeatRule;
    }
}
