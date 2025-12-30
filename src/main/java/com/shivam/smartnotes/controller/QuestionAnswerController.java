package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.service.QuestionAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionAnswerController {
    private final QuestionAnswerService questionAnswerService;

    public QuestionAnswerController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    // Stateless Q&A (no persistence)
    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @RequestParam Long userId,
            @RequestParam String question
    ) {
        String answer =
                questionAnswerService.askQuestion(userId, question);

        return ResponseEntity.ok(answer);
    }
}
