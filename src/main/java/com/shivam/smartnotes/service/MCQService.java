package com.shivam.smartnotes.service;

import com.shivam.smartnotes.dto.MCQResponse;

public interface MCQService {

    MCQResponse generateMcqs(
            Long noteId,
            String username,
            String topic,
            int count
    );

    MCQResponse getMcqsByTopic(
            String username,
            String topic
    );
}
