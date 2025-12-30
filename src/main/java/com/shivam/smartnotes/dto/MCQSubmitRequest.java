package com.shivam.smartnotes.dto;

import lombok.Data;

import java.util.List;

@Data
public class MCQSubmitRequest {

    private Long userId;
    private List<MCQAnswerRequest> answers;

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public List<McqAnswerRequest> getAnswers() {
//        return answers;
//    }
//
//    public void setAnswers(List<McqAnswerRequest> answers) {
//        this.answers = answers;
//    }
}
