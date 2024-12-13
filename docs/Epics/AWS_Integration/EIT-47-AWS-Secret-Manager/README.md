# EIT-47 AWS Secret Manager

## Summary
As a developer, I want to securely store and manage sensitive application secrets using AWS Secrets Manager so that credentials and API keys are not hardcoded or exposed.

## User Story
- **Title**: AWS Secrets Manager Integration  
- **Epic**: AWS Infrastructure Setup  
- **Description**: Use AWS Secrets Manager to securely manage and retrieve application secrets, such as database credentials and API keys, during runtime.

## Acceptance Criteria
- **Given** an AWS Secrets Manager configuration  
- **When** the application initializes  
- **Then** it should securely retrieve required secrets from AWS Secrets Manager.  
- **When** secrets are updated in AWS Secrets Manager  
- **Then** the application can access the updated secrets without requiring code changes.

## Implementation Details
AWS Secrets Manager is used to store secrets like database credentials, Cognito client secrets, and API keys. The application retrieves these secrets, ensuring security and ease of management.

## Screenshots
![Secrets Manager Configuration](./screenshots/secrets-manager-configuration.png)

## Related Links
- [Parent Epic: AWS Integration](../README.md)
