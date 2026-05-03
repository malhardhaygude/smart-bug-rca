package com.smart.bugrca.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bug")
@Getter
@Setter
@NoArgsConstructor
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bug_id", nullable = false, unique = true)
    private String bugId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH

    @Column(name="step_to_reproduce")
    private String stepsToReproduce;

    @Column(name="cmt_by_dev")
    private String commentByDev;

    @Column(name = "root_cause", columnDefinition = "text")
    private String rootCause;

    @Column(name = "impact", columnDefinition = "text")
    private String impact;

    @Column(name = "resolution", columnDefinition = "text")
    private String resolution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "last_updated_time", nullable = false)
    private LocalDateTime lastUpdatedTime;

    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdTime = now;
        this.lastUpdatedTime = now;

        if (this.status == null) {
            this.status = BugStatus.BACKLOG;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedTime = LocalDateTime.now();
    }
}