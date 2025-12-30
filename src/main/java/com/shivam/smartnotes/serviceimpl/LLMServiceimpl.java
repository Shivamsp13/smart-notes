package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.LLMRequest;
import com.shivam.smartnotes.dto.LLMResponse;
import com.shivam.smartnotes.service.LLMService;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class LLMServiceimpl implements LLMService {
    private static final String LLM_API_URL= "http://localhost:9000/generate";

    private final RestTemplate restTemplate;

    public LLMServiceimpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(String systemPrompt, String userPrompt) {

        LLMRequest request=new LLMRequest(systemPrompt,userPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<LLMRequest> requestEntity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<LLMResponse> response =
                    restTemplate.exchange(
                            LLM_API_URL,
                            HttpMethod.POST,
                            requestEntity,
                            LLMResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        "LLM API failed with status: " + response.getStatusCode()
                );
            }

            if (response.getBody() == null ||
                    response.getBody().getText() == null ||
                    response.getBody().getText().isBlank()) {
                throw new RuntimeException("Empty response from LLM");
            }

            return response.getBody().getText();

        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to call LLM service", ex);
        }
    }
}
