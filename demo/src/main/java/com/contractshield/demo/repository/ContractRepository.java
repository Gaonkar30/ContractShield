package com.contractshield.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contractshield.demo.model.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    
    // Find contracts by filename
    List<Contract> findByFilename(String filename);
    
    // Find contracts by risk level
    List<Contract> findByRiskLevel(String riskLevel);
    
    // Find contracts uploaded after a certain date
    List<Contract> findByUploadTimestampAfter(LocalDateTime timestamp);
    
    // Find contracts with validation score above a threshold
    @Query("SELECT c FROM Contract c WHERE c.validationScore >= :minScore")
    List<Contract> findByValidationScoreGreaterThanEqual(@Param("minScore") Double minScore);
    
    // Find contracts by content type
    List<Contract> findByContentType(String contentType);
    
    // Count contracts by risk level
    @Query("SELECT c.riskLevel, COUNT(c) FROM Contract c GROUP BY c.riskLevel")
    List<Object[]> countContractsByRiskLevel();
}
