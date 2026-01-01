package com.shivam.smartnotes.service;

import com.shivam.smartnotes.dto.MCQResponse;

public interface MCQService {

    MCQResponse generateMcqs(Long userId, String topic, int count);
    MCQResponse getMcqsByTopic(Long userId, String topic);
}
