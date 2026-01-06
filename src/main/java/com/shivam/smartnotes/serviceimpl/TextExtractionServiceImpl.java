package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.service.TextExtractionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.InputStream;

@Service
public class TextExtractionServiceImpl implements TextExtractionService {

    private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 500 MB
    private static final int MAX_PAGE_COUNT = 100;

    @Override
    public String extractText(MultipartFile file) {


        if(file==null || file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is Empty");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "PDF file size exceeds limit of 50 MB"
            );
        }

        try(InputStream inputStream=file.getInputStream();
            PDDocument document=PDDocument.load(inputStream)
            ) {

            int pageCount = document.getNumberOfPages();
            if (pageCount > MAX_PAGE_COUNT) {
                throw new IllegalArgumentException(
                        "PDF has too many pages. Maximum allowed is " + MAX_PAGE_COUNT
                );
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);

            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new IllegalArgumentException("PDF contains no readable text");
            }

            return extractedText.trim();

        }
        catch(Exception e){
            throw new RuntimeException("Failed to extract text from PDF");
        }
    }
}
