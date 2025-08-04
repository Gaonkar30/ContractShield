package com.contractshield.demo.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractorService {
    
    private final Tika tika;
    
    public TextExtractorService() {
        this.tika = new Tika();
    }
    
    public String extractText(MultipartFile file) throws IOException, TikaException {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        }
    }
    
    public boolean isSupportedFileType(String contentType) {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.equals("application/msword") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
            contentType.equals("text/plain")
        );
    }
}
