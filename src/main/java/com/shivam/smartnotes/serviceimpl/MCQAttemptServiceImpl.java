package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.*;
import com.shivam.smartnotes.entity.MCQ;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.AccessDeniedException;
import com.shivam.smartnotes.repository.MCQRepository;
import com.shivam.smartnotes.repository.UserRepository;
import com.shivam.smartnotes.security.SecurityUtil;
import com.shivam.smartnotes.service.MCQAttemptService;
import com.shivam.smartnotes.service.MCQService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MCQAttemptServiceImpl implements MCQAttemptService {

    private final MCQRepository mcqRepository;
    private final UserRepository userRepository;


    public MCQAttemptServiceImpl(MCQRepository mcqRepository, UserRepository userRepository) {
        this.mcqRepository = mcqRepository;
        this.userRepository=userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public MCQSubmitResponse submitMcqs(MCQSubmitRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int score = 0;
        List<MCQResultItem> results = new ArrayList<>();

        for (MCQAnswerRequest answer : request.getAnswers()) {

            MCQ mcq = mcqRepository.findById(answer.getMcqId())
                    .orElseThrow(() ->
                            new RuntimeException("MCQ not found: " + answer.getMcqId())
                    );

            if (!mcq.getChunk()
                    .getNote()
                    .getOwner()
                    .getUserId()
                    .equals(user.getUserId())) {

                throw new AccessDeniedException(
                        "You do not have access to MCQ: " + answer.getMcqId()
                );
            }

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
