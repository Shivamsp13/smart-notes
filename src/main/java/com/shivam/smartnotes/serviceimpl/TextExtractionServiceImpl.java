package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.service.TextExtractionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.InputStream;

@Service
public class TextExtractionServiceImpl implements TextExtractionService {

    @Override
    public String extractText(MultipartFile file) {
        if(file==null || file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is Empty");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }

        try(InputStream inputStream=file.getInputStream();
            PDDocument document=PDDocument.load(inputStream)
            ) {
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
