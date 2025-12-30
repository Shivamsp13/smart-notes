package com.shivam.smartnotes.service;

import com.shivam.smartnotes.dto.MCQSubmitRequest;
import com.shivam.smartnotes.dto.MCQSubmitResponse;

public interface MCQAttemptService {

    MCQSubmitResponse submitMcqs(MCQSubmitRequest request);

}
