package com.smart.bugrca.controller;

import com.smart.bugrca.dto.BugRequest;
import com.smart.bugrca.dto.BugResponse;
import com.smart.bugrca.model.Bug;
import com.smart.bugrca.model.BugStatus;
import com.smart.bugrca.service.BugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Bug APIs", description = "Bug management and RCA operations")
@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {
    private final BugService bugService;

    @Operation(summary = "Create a new bug with AI-powered RCA")
    @PostMapping
    public BugResponse createBug(@RequestBody BugRequest bugRequest){
        Bug bug = new Bug();
        bug.setBugId(bugRequest.bugId());
        bug.setDescription(bugRequest.description());
        bug.setSeverity(bugRequest.severity());

       Bug saved = bugService.createBug(bug, bugRequest.environment(),bugRequest.symptoms());

       return mapToResponse(saved);

    }

    //GET all bugs
    @Operation(summary = "Fetch all bugs")
    @GetMapping
    public List<BugResponse> getAllBugs() {
        return bugService.getAllBugs()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //GET bug by ID
    @Operation(summary = "Fetch bug by ID")
    @GetMapping("/{id}")
    public BugResponse getBugById(@PathVariable Long id) {
        Bug incident = bugService.getBugById(id);
        return mapToResponse(incident);
    }

    //UPDATE bug status
    @Operation(summary = "Update bug status")
    @PatchMapping("/{id}/status")
    public BugResponse updateStatus(
            @PathVariable Long id,
            @RequestParam BugStatus status) {

        Bug incident = bugService.updateStatus(id, status);
        return mapToResponse(incident);
    }


    private BugResponse mapToResponse(Bug bug) {
         return new BugResponse(
                 bug.getId(),bug.getBugId(),bug.getDescription(),bug.getSeverity(),
                 bug.getRootCause(),bug.getImpact(),bug.getResolution(),
                 bug.getStatus(),bug.getCreatedTime(),bug.getResolvedTime()
         );
    }

}
