package com.contractshield.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contractshield.demo.dto.ValidationReport;
import com.contractshield.demo.model.Contract;
import com.contractshield.demo.repository.ContractRepository;
import com.contractshield.demo.services.ClauseValidatorService;
import com.contractshield.demo.services.S3StorageService;
import com.contractshield.demo.services.TextExtractorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class ContractController {
    
    private final TextExtractorService textExtractorService;
    private final ClauseValidatorService clauseValidatorService;
    private final ContractRepository contractRepository;
    private final S3StorageService s3StorageService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public ContractController(TextExtractorService textExtractorService,
                            ClauseValidatorService clauseValidatorService,
                            ContractRepository contractRepository,
                            S3StorageService s3StorageService) {
        this.textExtractorService = textExtractorService;
        this.clauseValidatorService = clauseValidatorService;
        this.contractRepository = contractRepository;
        this.s3StorageService = s3StorageService;
        this.objectMapper = new ObjectMapper();
    }
    
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
            
            // Generate contract ID first
            String contractId = UUID.randomUUID().toString();
            
            // Create S3 object key with timestamp and contract ID for uniqueness
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String s3ObjectKey = String.format("contracts/%s/%s_%s%s", 
                contractId.substring(0, 8),
                timestamp,
                contractId,
                fileExtension
            );
            
            // Upload file to S3 FIRST
            try {
                s3StorageService.uploadFile(s3ObjectKey, file);
            } catch (IOException e) {
                response.put("error", "Failed to upload file to S3: " + e.getMessage());
                return ResponseEntity.internalServerError().body(response);
            }
            
            // Extract text from file (after successful S3 upload)
            String extractedText = textExtractorService.extractText(file);
            
            // Validate contract clauses
            ValidationReport validationReport = clauseValidatorService.validateContract(extractedText, contractId);
            
            // Create and populate Contract entity
            Contract contract = new Contract();
            contract.setId(contractId);
            contract.setFilename(file.getOriginalFilename());
            contract.setContentType(file.getContentType());
            contract.setFileSize(file.getSize());
            contract.setS3ObjectKey(s3ObjectKey);
            contract.setExtractedText(extractedText);
            contract.setValidationScore(validationReport.getScore());
            contract.setRiskLevel(validationReport.getRisk());
            
            // Convert clause results to JSON string for storage
            try {
                String clauseResultsJson = objectMapper.writeValueAsString(validationReport.getClauses());
                contract.setClauseResults(clauseResultsJson);
            } catch (JsonProcessingException e) {
                contract.setClauseResults("{}");
                System.err.println("Error converting clause results to JSON: " + e.getMessage());
            }
            
            // Save contract to database
            Contract savedContract = contractRepository.save(contract);
            
            // Build response
            response.put("contractId", savedContract.getId());
            response.put("filename", savedContract.getFilename());
            response.put("size", savedContract.getFileSize());
            response.put("contentType", savedContract.getContentType());
            response.put("s3ObjectKey", savedContract.getS3ObjectKey());
            response.put("extractedText", savedContract.getExtractedText());
            response.put("textLength", savedContract.getTextLength());
            response.put("validationReport", validationReport);
            response.put("uploadTimestamp", savedContract.getUploadTimestamp());
            response.put("analysisTimestamp", savedContract.getAnalysisTimestamp());
            response.put("message", "Contract uploaded to S3, analyzed and saved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Failed to process file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Helper method to extract file extension
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }
    
    @GetMapping("/contracts")
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/contracts/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable String id) {
        Optional<Contract> contract = contractRepository.findById(id);
        
        if (contract.isPresent()) {
            return ResponseEntity.ok(contract.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/contracts/risk/{riskLevel}")
    public ResponseEntity<List<Contract>> getContractsByRisk(@PathVariable String riskLevel) {
        List<Contract> contracts = contractRepository.findByRiskLevel(riskLevel.toUpperCase());
        return ResponseEntity.ok(contracts);
    }

    @DeleteMapping("/contracts/{id}")
    public ResponseEntity<Map<String, String>> deleteContract(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        
        if (contractRepository.existsById(id)) {
            contractRepository.deleteById(id);
            response.put("message", "Contract deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Contract not found");
            return ResponseEntity.notFound().build();
        }
    }
}