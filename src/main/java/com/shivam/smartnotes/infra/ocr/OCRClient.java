package com.shivam.smartnotes.infra.ocr;

import java.util.Optional;

public interface OCRClient {
    Optional<String> extractText(byte[] imageBytes, int pageIndex);
}
