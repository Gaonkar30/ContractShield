package com.contractshield.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "contracts")
public class Contract {
    
    @Id
    private String id; // Using UUID as primary key
    
    @Column(name = "filename", nullable = false)
    private String filename;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    // NEW: S3 object key for file storage
    @Column(name = "s3_object_key")
    private String s3ObjectKey;
    
    @Lob
    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;
    
    @Column(name = "text_length")
    private Integer textLength;
    
    // Store clause results as JSON string
    @Lob
    @Column(name = "clause_results", columnDefinition = "TEXT")
    private String clauseResults;
    
    @Column(name = "validation_score")
    private Double validationScore;
    
    @Column(name = "risk_level")
    private String riskLevel;
    
    @Column(name = "upload_timestamp")
    private LocalDateTime uploadTimestamp;
    
    @Column(name = "analysis_timestamp")
    private LocalDateTime analysisTimestamp;
    
    // Constructors
    public Contract() {
        this.uploadTimestamp = LocalDateTime.now();
        this.analysisTimestamp = LocalDateTime.now();
    }
    
    public Contract(String id, String filename, String contentType, Long fileSize) {
        this();
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
    
    // Existing getters and setters...
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    // NEW: S3 object key getter and setter
    public String getS3ObjectKey() {
        return s3ObjectKey;
    }
    
    public void setS3ObjectKey(String s3ObjectKey) {
        this.s3ObjectKey = s3ObjectKey;
    }
    
    public String getExtractedText() {
        return extractedText;
    }
    
    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
        this.textLength = extractedText != null ? extractedText.length() : 0;
    }
    
    public Integer getTextLength() {
        return textLength;
    }
    
    public void setTextLength(Integer textLength) {
        this.textLength = textLength;
    }
    
    public String getClauseResults() {
        return clauseResults;
    }
    
    public void setClauseResults(String clauseResults) {
        this.clauseResults = clauseResults;
    }
    
    public Double getValidationScore() {
        return validationScore;
    }
    
    public void setValidationScore(Double validationScore) {
        this.validationScore = validationScore;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public LocalDateTime getUploadTimestamp() {
        return uploadTimestamp;
    }
    
    public void setUploadTimestamp(LocalDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
    
    public LocalDateTime getAnalysisTimestamp() {
        return analysisTimestamp;
    }
    
    public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) {
        this.analysisTimestamp = analysisTimestamp;
    }
}
