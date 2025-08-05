package com.contractshield.demo.services;

import com.contractshield.demo.config.ContractShieldProperties;
import com.contractshield.demo.dto.ValidationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ClauseValidatorService {
    
    @Autowired
    private ContractShieldProperties contractShieldProperties;
    
    public ValidationReport validateContract(String contractText, String contractId) {
        Map<String, String> clauseRules = contractShieldProperties.getClauses();
        Map<String, String> clauseResults = new HashMap<>();
        int clausesFound = 0;
        
        // 1. Check each clause rule against the contract text
        for (Map.Entry<String, String> entry : clauseRules.entrySet()) {
            String clauseName = entry.getKey();
            String regexPattern = entry.getValue();
            
            Pattern pattern = Pattern.compile(regexPattern);
            if (pattern.matcher(contractText).find()) {
                clauseResults.put(clauseName, "present");
                clausesFound++;
            } else {
                clauseResults.put(clauseName, "missing");
            }
        }
        
        // 2. Calculate score (percentage of clauses found)
        double score = (double) clausesFound / clauseRules.size() * 100.0;
        
        // 3. Determine risk level based on score
        String risk = "HIGH";
        if (score > 80) {
            risk = "LOW";
        } else if (score > 50) {
            risk = "MEDIUM";
        }
        
        // 4. Build the final report
        ValidationReport report = new ValidationReport();
        report.setContractId(contractId);
        report.setClauses(clauseResults);
        report.setScore(Math.round(score * 100.0) / 100.0); // Round to 2 decimal places
        report.setRisk(risk);
        
        return report;
    }
    
    // Helper method to check if a specific clause exists
    public boolean hasClause(String contractText, String clauseType) {
        Map<String, String> clauseRules = contractShieldProperties.getClauses();
        String pattern = clauseRules.get(clauseType);
        
        if (pattern != null) {
            Pattern regex = Pattern.compile(pattern);
            return regex.matcher(contractText).find();
        }
        
        return false;
    }
    
    // Helper method to get clause details
    public Map<String, Object> getClauseDetails(String contractText, String clauseType) {
        Map<String, Object> details = new HashMap<>();
        Map<String, String> clauseRules = contractShieldProperties.getClauses();
        String pattern = clauseRules.get(clauseType);
        
        if (pattern != null) {
            Pattern regex = Pattern.compile(pattern);
            boolean found = regex.matcher(contractText).find();
            
            details.put("clauseType", clauseType);
            details.put("pattern", pattern);
            details.put("status", found ? "present" : "missing");
        }
        
        return details;
    }
}
