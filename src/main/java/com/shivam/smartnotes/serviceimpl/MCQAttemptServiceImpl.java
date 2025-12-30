package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.*;
import com.shivam.smartnotes.entity.MCQ;
import com.shivam.smartnotes.repository.MCQRepository;
import com.shivam.smartnotes.service.MCQAttemptService;
import com.shivam.smartnotes.service.MCQService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MCQAttemptServiceImpl implements MCQAttemptService {

    private final MCQRepository mcqRepository;

    public MCQAttemptServiceImpl(MCQRepository mcqRepository) {
        this.mcqRepository = mcqRepository;
    }

    @Override
    public MCQSubmitResponse submitMcqs(MCQSubmitRequest request) {

        int score = 0;
        List<MCQResultItem> results = new ArrayList<>();

        for (MCQAnswerRequest answer : request.getAnswers()) {

            MCQ mcq = mcqRepository.findById(answer.getMcqId())
                    .orElseThrow(() ->
                            new RuntimeException("MCQ not found: " + answer.getMcqId())
                    );

            boolean correct =
                    mcq.getCorrectOptionIndex() == answer.getSelectedOptionIndex();

            if (correct) score++;

            MCQResultItem item = new MCQResultItem();
            item.setQuestion(mcq.getQuestion());
            item.setOptions(mcq.getOptions());
            item.setCorrectOptionIndex(mcq.getCorrectOptionIndex());
            item.setSelectedOptionIndex(answer.getSelectedOptionIndex());
            item.setExplanation(mcq.getExplanation());
            item.setCorrect(correct);

            results.add(item);
        }

        MCQSubmitResponse response = new MCQSubmitResponse();
        response.setScore(score);
        response.setTotal(results.size());
        response.setResults(results);

        return response;
    }
}
