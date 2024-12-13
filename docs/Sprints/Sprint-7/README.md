# Sprint Review & Retrospective

This document provides an overview of the work completed, challenges faced, and improvements identified in Sprint 7.
[December 2nd - December 9th]
---

## Sprint 7: AWS Integration and Deploy (AWS Secret Manager and Full Deploy)

### Story Points
- **To Do:** 0 points
- **In Progress:** 11 points
- **Done:** 6 points

### User Stories
- **Done:** 2

### Bugs
- **Done:** 1

### Sprint Goals
- AWS Secret Manager Integration
- RDS Database Configuration
- Resolve any frontend bugs/UI

### Completed Work
- [AWS Integration:](../../Epics/AWS_Integration/README.md)
  - Code with no UI bugs
  - Utilized AWS Secret manager to securely store and manage sensitive application secrets
  - Configured ECR's, EC2's, Load Balancers with https and the given domain.

### Outcomes
- Integration of AWS Secret Manager
- App deployed with the load balancers but missing the API Gateway
- Report started

### Challenges
- During each phase of the deployment there were some constraints in the development process, Docker problems, Load Balancers Target Groups, etc...
- The most challenging configuration was the API Gateway, I havent configured it fully and I am still having CORS errors, and other problems (as I have the load balancer comunicating with the backend but not the API Gateway)

### Improvements for Next Sprint
- Finalize the API Gateway configuration, to have the app fully deployed.
- Finalize the Report