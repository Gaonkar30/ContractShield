package com.contractshield.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contractshield.demo.services.TextExtractorService;

@RestController
@RequestMapping("/api")
public class ContractController {
    
    @Autowired
    private TextExtractorService textExtractorService;
    
    @PostMapping("/contracts")
    public ResponseEntity<Map<String, Object>> uploadContract(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if file type is supported
            if (!textExtractorService.isSupportedFileType(file.getContentType())) {
                response.put("error", "Unsupported file type: " + file.getContentType());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Extract text from file
            String extractedText = textExtractorService.extractText(file);
            
            // Basic file info
            response.put("filename", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("extractedText", extractedText);
            response.put("textLength", extractedText.length());
            response.put("message", "File processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Failed to process file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}