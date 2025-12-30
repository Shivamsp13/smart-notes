package com.shivam.smartnotes.service;

import com.shivam.smartnotes.entity.MCQ;
import java.util.List;

public interface MCQService {

    List<MCQ> generateMcqs(Long userId,String topic,int count);
    List<MCQ> getMcqsByTopic(Long userId,String topic);
}
