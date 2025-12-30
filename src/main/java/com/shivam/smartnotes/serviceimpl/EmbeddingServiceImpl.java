package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.EmbeddingRequest;
import com.shivam.smartnotes.dto.EmbeddingResponse;
import com.shivam.smartnotes.service.EmbeddingService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final String EMBEDDING_API_URL="http://127.0.0.1:8000/embed";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EmbeddingServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }



    @Override
    public String generateEmbedding(String text) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(List.of(text));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmbeddingRequest> entity=
                    new HttpEntity<>(request,headers);

            ResponseEntity<EmbeddingResponse> response =
                    restTemplate.exchange(
                            EMBEDDING_API_URL,
                            HttpMethod.POST,
                            entity,
                            EmbeddingResponse.class
                    );

            if(response.getBody()==null ||
                response.getBody().getEmbeddings().isEmpty()){
                throw new RuntimeException("Empty Embedding Response from Embedding Service");
            }

            List<Float> vector=response.getBody()
                    .getEmbeddings()
                    .get(0);

            return serialize(vector);

        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to generate the embedding"+e);
        }
    }


    @Override
    public double cosineSimilarity(String emb1, String emb2) {
        float [] v1=deserialize(emb1);
        float [] v2=deserialize(emb2);

        if(v1.length!=v2.length){
            throw new IllegalArgumentException("Embedding dimension mismatch");
        }

        double dot = 0.0;
        double mag1 = 0.0;
        double mag2 = 0.0;

        for(int i=0;i< v1.length;i++){
            dot+=v1[i]*v2[i];
            mag1+=v1[i]*v1[i];
            mag2+=v2[i]*v2[i];
        }
        return dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    }

    private String serialize(List<Float> vector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vector.get(i));
        }
        return sb.toString();
    }

    private float[] deserialize(String embedding) {
        String[] parts = embedding.split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Float.parseFloat(parts[i]);
        }
        return vector;
    }


}
