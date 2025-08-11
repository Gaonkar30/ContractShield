ContractShield: Cloud-Native Contract Analysis API
==================================================

ContractShield is a secure, RESTful backend service built with Spring Boot that automates the analysis of legal documents. The system accepts contract uploads (PDF/DOCX), stores them securely in AWS S3, extracts the text, validates the presence of critical legal clauses, and saves a structured analysis report to a PostgreSQL database on AWS RDS.

This project is fully containerized with Docker and deployed on AWS App Runner within a secure, private cloud network (VPC).

Features
--------

*   **File Upload**: Accepts .pdf, .docx, and .txt file uploads through a REST API endpoint.
    
*   **Cloud Storage**: Securely stores all original contract documents in a private AWS S3 bucket.
    
*   **Text Extraction**: Parses and extracts text content from uploaded documents using Apache Tika.
    
*   **Clause Validation**: Scans extracted text for mandatory legal clauses (e.g., Indemnity, Arbitration, Penalty) using a configurable, rule-based engine.
    
*   **Risk Analysis**: Generates a risk score and level (HIGH, MEDIUM, LOW) based on the presence of required clauses.
    
*   **Persistent Storage**: Saves contract metadata and analysis reports to a PostgreSQL database on AWS RDS.
    
*   **CRUD Operations**: Provides API endpoints to create, retrieve, list, and delete contract records.
    
*   **Cloud-Native Deployment**: Containerized with **Docker** and deployed on **AWS App Runner** in a secure VPC configuration.
    

Tech Stack
----------

### Component Technology/Library
*  **Framework**: Spring Boot 3.5.4
*  **Language**: Java 21
*  **API Layer**: Spring Web (REST)
*  **Database**: PostgreSQL (AWS RDS)
*  **Data Access**: Spring Data JPA, Hibernate
*  **File Storage**: AWS S3
*  **Text Extraction**: Apache Tika
*  **Deployment**: Docker, AWS App Runner, AWS ECR
*  **Networking**: AWS VPC, NAT Gateway, VPC Endpoint
*  **Utilities**: Lombok, Jackson
*  **Health & Metrics**: Spring Boot Actuator

Setup and Configuration
-----------------------

### Prerequisites

*   Java 21
    
*   Apache Maven
    
*   Docker Desktop
    
*   An AWS Account
    
*   AWS CLI configured locally (aws configure)
    

### Local Development

Since H2 has been removed for a more production-like setup, you will need a local PostgreSQL instance running.

1.  git clone cd contractshield/demo
    
2.  YAML datasource: url: jdbc:postgresql://localhost:5432/contractshield\_db username: your\_local\_username password: your\_local\_password
    
3.  Create the database: In your PostgreSQL instance, create a new database named contractshield\_db.
    
4.  ./mvnw spring-boot:runThe application will be available at http://localhost:8081.
    

API Endpoints
-------------

All endpoints are available under the base path /api.

### **1\. Upload and Analyze a Contract**

*   **Endpoint**: POST /contracts
    
*   **Description**: Uploads a contract file, stores it in S3, extracts the text, performs clause analysis, and saves the results to the database.
    
*   **Request**: form-data with a single key:
    
    *   file: The .pdf, .docx, or .txt file to be analyzed.
        
*   JSON{ "contractId": "a1b2c3d4-e5f6-7890-1234-567890abcdef", "filename": "contract.docx", "size": 12345, "contentType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "s3ObjectKey": "contracts/a1b2c3d4/1662650000000\_a1b2c3d4-....docx", "extractedText": "This is the full extracted text...", "textLength": 1500, "validationReport": { "contractId": "a1b2c3d4-e5f6-7890-1234-567890abcdef", "clauses": { "indemnity": "present", "sla": "missing", "penalty": "present" }, "score": 66.7, "risk": "MEDIUM" }, "uploadTimestamp": "2025-08-10T10:00:00.000Z", "analysisTimestamp": "2025-08-10T10:00:01.000Z", "message": "Contract uploaded to S3, analyzed and saved successfully"}
    

### **2\. Get All Contracts**

*   **Endpoint**: GET /contracts
    
*   **Description**: Retrieves a list of all contract records stored in the database.
    
*   **Success Response (200 OK)**: A JSON array of Contract objects.
    

### **3\. Get a Specific Contract**

*   **Endpoint**: GET /contracts/{id}
    
*   **Description**: Retrieves a single contract record by its UUID.
    
*   **Path Variable**:
    
    *   id (String): The UUID of the contract.
        
*   **Success Response (200 OK)**: A single Contract JSON object.
    
*   **Failure Response (404 Not Found)**: If no contract with the given ID exists.
    

### **4\. Get Contracts by Risk Level**

*   **Endpoint**: GET /contracts/risk/{riskLevel}
    
*   **Description**: Retrieves a list of contracts matching a specific risk level.
    
*   **Path Variable**:
    
    *   riskLevel (String): The risk level to filter by (e.g., HIGH, MEDIUM, LOW).
        
*   **Success Response (200 OK)**: A JSON array of matching Contract objects.
    

### **5\. Delete a Contract**

*   **Endpoint**: DELETE /contracts/{id}
    
*   **Description**: Deletes a contract record from the database.
    
*   **Path Variable**:
    
    *   id (String): The UUID of the contract to delete.
        
*   JSON{ "message": "Contract deleted successfully"}
    
*   **Failure Response (404 Not Found)**: If no contract with the given ID exists.
    

Deployment
----------

This application is designed for cloud deployment and is containerized using the provided Dockerfile. The file includes a multi-stage build to create a lightweight, secure production image.

The project has been successfully deployed on **AWS App Runner**, using a secure VPC configuration with a private **AWS RDS PostgreSQL** instance and an egress-only network path via a **NAT Gateway** and **S3 Gateway Endpoint**.
