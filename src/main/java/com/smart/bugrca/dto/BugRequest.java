package com.smart.bugrca.dto;

import java.util.List;

public record BugRequest(
        String bugId,
        String description,
        String severity,
        String environment,
        List<String> symptoms,
        String commentByDev,
        String stepsToReproduce
) {}