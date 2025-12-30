package com.shivam.smartnotes.dto;

public class MCQAnswerRequest {

    private Long mcqId;
    private int selectedOptionIndex;

    public Long getMcqId() {
        return mcqId;
    }

    public void setMcqId(Long mcqId) {
        this.mcqId = mcqId;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }
}
