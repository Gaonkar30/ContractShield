package com.contractshield.demo.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "contractshield")
public class ContractShieldProperties {
    
    private Map<String, String> clauses;
    
    public Map<String, String> getClauses() {
        return clauses;
    }
    
    public void setClauses(Map<String, String> clauses) {
        this.clauses = clauses;
    }
}
