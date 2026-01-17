package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.MCQGenerateRequest;
import com.shivam.smartnotes.dto.MCQResponse;
import com.shivam.smartnotes.dto.MCQSubmitRequest;
import com.shivam.smartnotes.dto.MCQSubmitResponse;
import com.shivam.smartnotes.security.SecurityUtil;
import com.shivam.smartnotes.service.MCQAttemptService;
import com.shivam.smartnotes.service.MCQService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcqs")
public class MCQController {

    private final MCQService mcqService;
    private final MCQAttemptService mcqAttemptService;

    public MCQController(
            MCQService mcqService,
            MCQAttemptService mcqAttemptService
    ) {
        this.mcqService = mcqService;
        this.mcqAttemptService = mcqAttemptService;
    }

    @PostMapping("/generatemcq")
    public ResponseEntity<MCQResponse> generateMcqs(
            @Valid @RequestBody MCQGenerateRequest request
    ) {
        String username = SecurityUtil.getCurrentUsername();
        MCQResponse response =
                mcqService.generateMcqs(
                        request.getNoteId(),
                        username,
                        request.getTopic(),
                        request.getCount()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<MCQResponse> getMcqsByTopic(
            @RequestParam String topic
    ) {
        String username = SecurityUtil.getCurrentUsername();

        MCQResponse response =
                mcqService.getMcqsByTopic(username, topic);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<MCQSubmitResponse> submitMcqs(
            @Valid @RequestBody MCQSubmitRequest request
    ) {
        MCQSubmitResponse response =
                mcqAttemptService.submitMcqs(request);

        return ResponseEntity.ok(response);
    }
}
