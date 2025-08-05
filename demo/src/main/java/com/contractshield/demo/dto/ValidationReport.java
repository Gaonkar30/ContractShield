package com.contractshield.demo.dto;

import java.util.Map;

public class ValidationReport {
    
    private String contractId; // We'll populate this later
    private Map<String, String> clauses; // e.g., {"indemnity": "present", "sla": "missing"}
    private double score;
    private String risk;
    
    public ValidationReport() {}
    
    public ValidationReport(String contractId, Map<String, String> clauses, double score, String risk) {
        this.contractId = contractId;
        this.clauses = clauses;
        this.score = score;
        this.risk = risk;
    }
    
    // Getters and Setters
    public String getContractId() {
        return contractId;
    }
    
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
    public Map<String, String> getClauses() {
        return clauses;
    }
    
    public void setClauses(Map<String, String> clauses) {
        this.clauses = clauses;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public String getRisk() {
        return risk;
    }
    
    public void setRisk(String risk) {
        this.risk = risk;
    }
}
