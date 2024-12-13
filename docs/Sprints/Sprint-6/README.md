# Sprint Review & Retrospective

This document provides an overview of the work completed, challenges faced, and improvements identified in Sprint 6.
[November 25th - December 2nd]
---

## Sprint 6: AWS Integration (Authentication, Initialize AWS Deployment)

### Story Points
- **To Do:** 0 points
- **In Progress:** 0 points
- **Done:** 11 points

### User Stories
- **Done:** 3

### Task
- **Done:** 1

### Bug
- **Done:** 1

### Sprint Goals
- AWS Integration:
  - IDP Authentication
  - Initialize AWS Deplyment (Create Vpc, Subnets, Security Groups)
- Frontend Dockerization and Deployment

### Completed Work
- [Project Configuration:](../../Epics/Project_Configuration/README.md)
  - Using Nginx and Docker I containerized the frontend app, so it would be possible to deploy into the Cloud
- [AWS Integration:](../../Epics/AWS_Integration/README.md)
  - Utilized Cognito UI for the authentication.
  - Utilized Cognito to Register and Authenticate to my application.
  - Created and configured the VPC, Subnets, Security, Groups

### Outcomes
- Authentication using Cognito IDP, with token exchange.
- AWS Deploy Configuration initialized.
- Frontend application into a docker container.
- Code cleaned and organized.
- Switched from Learners Lab into Self Account

### Challenges
- I previously had configured JWT Authentication (Services, Controllers,..) it was a little difficult to understand and organize my code to use the Cognito exchange of codes/tokens for authentication.
- Learners Lab had many restriction and errors and I was encouraged to change to a Self Account, this took some time to configure everything again.

### Improvements for Next Sprint
- Advance in the AWS Deploy (backend, frontend, database)