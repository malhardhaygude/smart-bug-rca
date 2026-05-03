package com.smart.bugrca.service;

import com.smart.bugrca.llm.LlmService;
import com.smart.bugrca.model.Bug;
import com.smart.bugrca.model.BugStatus;
import com.smart.bugrca.repository.BugRepository;
import com.smart.bugrca.utility.RcaSections;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;
    private final LlmService llmService;
    private final RcaSections rcaSections;

    //Create Bug with Rule-based + AI RCA
    public Bug createBug(
            Bug bug,
            String environment,
            List<String> symptoms
    ) {
        bug.setStatus(BugStatus.BACKLOG);

        //Rule-based RCA (fallback)
        applyRuleBasedRca(bug);

        //AI-powered RCA (override if successful)
        try {
            String aiRca = llmService.generateRca(
                    bug.getDescription(),
                    bug.getSeverity(),
                    environment,
                    symptoms
            );
            rcaSections.setRacVariables(aiRca);
            bug.setRootCause(rcaSections.getRca());
            bug.setImpact(rcaSections.getImpact());
            bug.setResolution(rcaSections.getResolution());
        } catch (Exception e) {
            // fallback RCA remains
            System.out.println("Exception: "+ e);
        }

        return bugRepository.save(bug);
    }

    private void applyRuleBasedRca(Bug bug) {
        if (bug.getDescription() == null) return;

        String category = getCategory(bug);

        switch (category) {
            case "CPU" -> bug.setRootCause("High CPU usage detected");

            case "MEMORY" -> bug.setRootCause("Memory exhaustion / possible memory leak");

            case "DISK" -> bug.setRootCause("Disk space exhaustion");

            case "DATABASE" -> bug.setRootCause("Database connectivity or outage issue");

            case "TIMEOUT" -> bug.setRootCause("Service timeout / latency issue");

            case "NETWORK" -> bug.setRootCause("Network connectivity issue");

            case "NPE" -> bug.setRootCause("Null pointer exception in application");

            case "SERVER_ERROR" -> bug.setRootCause("Unhandled server-side exception");

            case "NOT_FOUND" -> bug.setRootCause("Resource not found / incorrect endpoint");

            case "AUTH" -> bug.setRootCause("Authentication / authorization failure");

            case "DEPLOYMENT" -> bug.setRootCause("Deployment or CI/CD pipeline failure");

            case "CONFIG" -> bug.setRootCause("Configuration issue");

            case "THREAD" -> bug.setRootCause("Thread contention / deadlock detected");

            case "API" -> bug.setRootCause("External API/service failure");

            default -> bug.setRootCause("Cause unclear - manual investigation required");
        }
    }

    private static @NonNull String getCategory(Bug bug) {
        String desc = bug.getDescription().toLowerCase();

        String category;

        if (desc.contains("cpu") || desc.contains("high load")) {
            category = "CPU";
        } else if (desc.contains("memory") || desc.contains("outofmemory") || desc.contains("heap")) {
            category = "MEMORY";
        } else if (desc.contains("disk") || desc.contains("storage") || desc.contains("no space")) {
            category = "DISK";
        } else if (desc.contains("database") || desc.contains("sql") || desc.contains("connection refused")) {
            category = "DATABASE";
        } else if (desc.contains("timeout") || desc.contains("timed out")) {
            category = "TIMEOUT";
        } else if (desc.contains("network") || desc.contains("dns") || desc.contains("unreachable")) {
            category = "NETWORK";
        } else if (desc.contains("nullpointer") || desc.contains("null pointer")) {
            category = "NPE";
        } else if (desc.contains("500") || desc.contains("internal server error")) {
            category = "SERVER_ERROR";
        } else if (desc.contains("404") || desc.contains("not found")) {
            category = "NOT_FOUND";
        } else if (desc.contains("auth") || desc.contains("unauthorized") || desc.contains("403")) {
            category = "AUTH";
        } else if (desc.contains("deployment") || desc.contains("build") || desc.contains("pipeline")) {
            category = "DEPLOYMENT";
        } else if (desc.contains("config") || desc.contains("configuration")) {
            category = "CONFIG";
        } else if (desc.contains("thread") || desc.contains("deadlock")) {
            category = "THREAD";
        } else if (desc.contains("api") || desc.contains("third-party") || desc.contains("external service")) {
            category = "API";
        } else {
            category = "UNKNOWN";
        }
        return category;
    }

    //Read operations
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public Bug getBugById(Long id) {
        return bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    //Status lifecycle handler
    public Bug updateStatus(Long id, BugStatus status) {
        Bug incident = getBugById(id);

        switch (status) {
            case IN_PROGRESS -> incident.setStatus(BugStatus.IN_PROGRESS);

            case COMPLETE -> {
                if (incident.getResolvedTime() == null) {
                    incident.setResolvedTime(LocalDateTime.now());
                }
                incident.setStatus(BugStatus.COMPLETE);
            }

            case CLOSED -> {
                if (incident.getResolvedTime() == null) {
                    throw new IllegalStateException("Resolve incident before closing");
                }
                incident.setStatus(BugStatus.CLOSED);
            }

            default -> throw new IllegalArgumentException("Invalid status");
        }

        return bugRepository.save(incident);
    }

}
