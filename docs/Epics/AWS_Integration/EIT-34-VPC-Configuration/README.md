# EIT-34 VPC Configuration

## Summary
As a developer, I want to configure a Virtual Private Cloud (VPC) so that the application infrastructure is secure, scalable, and properly segmented.

## User Story
- **Title**: VPC Configuration  
- **Epic**: AWS Infrastructure Setup  
- **Description**: Configure a VPC with subnets, routing tables, and security groups to ensure secure and efficient communication between application components.

## Acceptance Criteria
- **Given** an AWS account  
- **When** I configure a VPC with 2 public and 2 private subnets  
- **Then** the application components (frontend, backend, database) should communicate securely within the VPC.  
- **When** security groups are applied  
- **Then** unauthorized access to sensitive resources is prevented.

## Implementation Details
The VPC includes 2 public subnets and 2 private subnets across 2 different AZ. Appropriate security group rules and route tables are configured to allow secure communication.

## Screenshots
![VPC Diagram](./screenshots/vpc-diagram.png)

## Related Links
- [Parent Epic: AWS Integration](../README.md)
