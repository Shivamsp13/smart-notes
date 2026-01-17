package com.shivam.smartnotes.serviceimpl;
import com.shivam.smartnotes.infra.ocr.OCRClient;
import com.shivam.smartnotes.service.TextExtractionService;
import com.shivam.smartnotes.utils.TextQualityEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.shivam.smartnotes.infra.ocr.OCRProperties;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

@Service
public class TextExtractionServiceImpl implements TextExtractionService {

    private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 50 MB
    private static final int MAX_PAGE_COUNT = 1000;

    private final OCRClient ocrClient;
    private final OCRProperties ocrProperties;


    public TextExtractionServiceImpl(OCRClient ocrClient,OCRProperties ocrProperties) {
        this.ocrClient = ocrClient;
        this.ocrProperties=ocrProperties;
    }

    @Override
    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("PDF file size exceeds limit of 50 MB");
        }

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            int pageCount = document.getNumberOfPages();
            if (pageCount > MAX_PAGE_COUNT) {
                throw new IllegalArgumentException(
                        "PDF has too many pages. Maximum allowed is " + MAX_PAGE_COUNT
                );
            }

            PDFRenderer renderer = new PDFRenderer(document);
            PDFTextStripper stripper = new PDFTextStripper();

            StringBuilder finalText = new StringBuilder();

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {

                stripper.setStartPage(pageIndex + 1);
                stripper.setEndPage(pageIndex + 1);

                String pageText = stripper.getText(document);

                if (!TextQualityEvaluator.isUsable(pageText)) {

                    BufferedImage image = renderer.renderImageWithDPI(
                            pageIndex,
                            ocrProperties.getRenderDPI()
                    );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);

                    var ocrResult = ocrClient.extractText(baos.toByteArray(), pageIndex);
                    if (ocrResult.isPresent()) {
                        pageText = ocrResult.get();
                    }

                }

                finalText.append(pageText).append("\n");
            }

            return finalText.toString().trim();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from PDF", e);
        }
    }
}
