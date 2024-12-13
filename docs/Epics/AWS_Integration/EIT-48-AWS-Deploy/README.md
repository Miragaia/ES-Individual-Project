# EIT-48 AWS Deploy

## Summary
As a developer, I want to deploy my application to AWS infrastructure so that it is accessible, and reliable for end users.

## User Story
- **Title**: Deploy Application on AWS  
- **Epic**: AWS Infrastructure Setup  
- **Description**: Use AWS services like EC2, ECR Load Balancers and API Gateway to deploy the frontend and backend applications and make them available to users.

## Acceptance Criteria
- **Given** an AWS infrastructure with EC2 instances, ECR, Load Balancers and API Gateway  
- **When** the application is deployed using Docker and Docker Compose  
- **Then** it should be accessible via the assigned domain and function as intended.  
- **When** updates are pushed to ECR  
- **Then** the deployment process should allow for seamless updates without significant downtime.

## Implementation Details
The application is deployed using Docker containers. The frontend and backend services are hosted on separate EC2 instances, managed by an Application Load Balancer for routing and scaling. AWS ECR is used for storing Docker images.

## Screenshots
![AWS Deploy Process](./screenshots/aws-deploy-process.png)

## Related Links
- [Parent Epic: AWS Integration](../README.md)
