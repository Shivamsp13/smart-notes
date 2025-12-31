package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.service.QuestionAnswerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shivam.smartnotes.dto.QuestionAskRequest;

@RestController
@RequestMapping("/questions")
public class QuestionAnswerController {
    private final QuestionAnswerService questionAnswerService;

    public QuestionAnswerController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @RequestParam Long userId,
            @Valid @RequestBody QuestionAskRequest request
    ) {
        String answer =
                questionAnswerService.askQuestion(userId, request.getQuestion());

        return ResponseEntity.ok(answer);
    }
}
