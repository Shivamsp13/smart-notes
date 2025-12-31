package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.MCQGenerateRequest;
import com.shivam.smartnotes.dto.MCQSubmitRequest;
import com.shivam.smartnotes.dto.MCQSubmitResponse;
import com.shivam.smartnotes.entity.MCQ;
import com.shivam.smartnotes.service.MCQAttemptService;
import com.shivam.smartnotes.service.MCQService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mcqs")
public class MCQController {
    private final MCQService mcqService;
    private final MCQAttemptService mcqAttemptService;


    public MCQController(MCQService mcqService, MCQAttemptService mcqAttemptService) {
        this.mcqService = mcqService;
        this.mcqAttemptService=mcqAttemptService;
    }

    @PostMapping("/generatemcq")
    public ResponseEntity<List<MCQ>> generateMcqs(
            @RequestParam Long userId,
            @Valid @RequestBody MCQGenerateRequest request
            ){
        List<MCQ> mcqs =
                mcqService.generateMcqs(userId, request.getTopic(), request.getCount());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mcqs);
    }


    @GetMapping
    public ResponseEntity<List<MCQ>> getMcqsByTopic(
            @RequestParam Long userId,
            @RequestParam String topic
    ) {
        List<MCQ> mcqs =
                mcqService.getMcqsByTopic(userId, topic);

        return ResponseEntity.ok(mcqs);
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
