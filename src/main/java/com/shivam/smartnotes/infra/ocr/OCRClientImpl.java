package com.shivam.smartnotes.infra.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OCRClientImpl implements OCRClient{

    private final @Qualifier("ocrRestTemplate") RestTemplate restTemplate;
    private final OCRProperties ocrProperties;

    @Override
    public Optional<String> extractText(byte[] imageBytes, int pageIndex) {

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "page_" + pageIndex + ".png";
                }
            };

            // Use MultiValueMap for proper multipart/form-data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", resource);  // ✅ Changed from "file" to "image"

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            String url = ocrProperties.getBaseURL() + "/ocr/extract";

            ResponseEntity<OCRResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity,
                            OCRResponse.class
                    );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getText() != null
                    && !response.getBody().getText().isBlank()) {

                return Optional.of(response.getBody().getText());
            }
        }
        catch (RestClientException e){
            log.warn("OCR failed for page {}: {}", pageIndex, e.getMessage());
        }
        catch (Exception e){
            log.warn("Unexpected OCR error for page {}: {}", pageIndex, e.getMessage());
        }
        return Optional.empty();

    }
}