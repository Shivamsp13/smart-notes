package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.QuestionAskRequest;
import com.shivam.smartnotes.security.SecurityUtil;
import com.shivam.smartnotes.service.QuestionAnswerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService;

    public QuestionAnswerController(
            QuestionAnswerService questionAnswerService
    ) {
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @Valid @RequestBody QuestionAskRequest request
    ) {
        String username = SecurityUtil.getCurrentUsername();
        String answer =
                questionAnswerService.askQuestion(
                        request.getNoteId(),
                        username,
                        request.getQuestion()
                );

        return ResponseEntity.ok(answer);
    }
}
