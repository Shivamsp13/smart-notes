package com.shivam.smartnotes.infra.ocr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="ocr")
public class OCRProperties {

    private String baseURL;

    private int connectTimeoutMs=2000;

    private int readTimeoutMs=8000;

    private int renderDPI=300;
}
