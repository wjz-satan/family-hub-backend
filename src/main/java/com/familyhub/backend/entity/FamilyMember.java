package com.familyhub.backend.entity;

import com.familyhub.backend.enums.FamilyRole;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "family_members", uniqueConstraints = @UniqueConstraint(columnNames = {"familyId", "userId"}))
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long familyId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FamilyRole role = FamilyRole.MEMBER;

    @Column(length = 20)
    private String alias;

    @Column(nullable = false)
    private Instant joinedAt = Instant.now();

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public FamilyRole getRole() {
        return role;
    }

    public void setRole(FamilyRole role) {
        this.role = role;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
