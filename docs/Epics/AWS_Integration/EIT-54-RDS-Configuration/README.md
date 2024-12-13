# EIT-54 AWS RDS Configuration  

## Summary  
As a developer, I want to configure an AWS RDS instance so that the application can store and manage data reliably and securely.  

## User Story  
- **Title**: AWS RDS Integration  
- **Epic**: AWS Infrastructure Setup  
- **Description**: Configure an AWS RDS PostgreSQL instance to handle persistent data storage for the application backend. Ensure the database is securely accessible by the backend for storing and retrieving user and task data.  

## Acceptance Criteria  
- **Given** an AWS account with RDS service enabled  
- **When** an RDS instance is configured with the required database schema  
- **Then** the backend should be able to connect to the database to store and retrieve data.  

- **When** the database is tested for connectivity and queries  
- **Then** it should return expected results without latency issues.  

## Implementation Details  
AWS RDS is used to deploy a managed PostgreSQL instance for secure and reliable data management.  

## Screenshots  
![RDS Instance Configuration](./screenshots/rds-instance-configuration.png)  

## Related Links  
- [Parent Epic: AWS Integration](../README.md)  
