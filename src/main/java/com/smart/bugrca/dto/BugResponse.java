package com.smart.bugrca.dto;

import com.smart.bugrca.model.BugStatus;

import java.time.LocalDateTime;

public record BugResponse(
        Long id,
        String bugId,
        String description,
        String severity,
        String rootCause,
        BugStatus status,
        LocalDateTime createdTime,
        LocalDateTime resolvedTime
) {}