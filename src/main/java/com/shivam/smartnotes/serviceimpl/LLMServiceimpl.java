package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.LLMRequest;
import com.shivam.smartnotes.dto.LLMResponse;
import com.shivam.smartnotes.service.LLMService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;



import java.util.List;

@Service
public class LLMServiceimpl implements LLMService {

    private static final String LLM_API_URL= "https://api.groq.com/openai/v1/chat/completions";

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate;

    public LLMServiceimpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    @Override
    public String generate(String systemPrompt, String userPrompt) {

        LLMRequest request = new LLMRequest(
                "llama-3.1-8b-instant",
                List.of(
                        new LLMRequest.Message("system", systemPrompt),
                        new LLMRequest.Message("user", userPrompt)
                ),
                0.2
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(groqApiKey);

        System.out.println("Groq key length = " + groqApiKey.length());
        System.out.println("Groq key prefix = " + groqApiKey.substring(0, 4));


        HttpEntity<LLMRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<LLMResponse> response =
                    restTemplate.exchange(
                            LLM_API_URL,
                            HttpMethod.POST,
                            entity,
                            LLMResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().getChoices() == null
                    || response.getBody().getChoices().isEmpty()) {

                throw new RuntimeException("Invalid response from Groq LLM");
            }

            return response.getBody()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to call Groq LLM service", ex);
        }
    }
}
