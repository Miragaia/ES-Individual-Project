# ToDoList Project

## Overview  
The **ToDoList** project is a task management application designed to streamline task creation, categorization, and tracking. It incorporates modern development practices, a robust AWS deployment architecture, and seamless user authentication.

---

## Table of Contents  
1. [Project Features](#project-features)  
2. [Architecture](#architecture)  
3. [Tech Stack](#tech-stack)  
4. [Development Workflow](#development-workflow)  
5. [Agile Methodology](#agile-methodology)  
6. [Deployment](#deployment)  
7. [Definitions](#definitions)  
    - [Definition of Ready](#definition-of-ready)  
    - [Definition of Done](#definition-of-done)  

---

## Project Features  

### Authentication
- Users can log in and register using AWS Cognito.  
- Secure token-based authentication ensures privacy.  

### Task Management  
- Create, edit, delete, and categorize tasks.  
- Filter tasks by category or status for efficient organization.  

### Categories  
- Custom categories to organize tasks effectively.  

---

## Architecture  

### Components  
- **Frontend:** Dockerized static application deployed on AWS EC2.  
- **Backend:** Spring Boot application deployed on AWS EC2 with a PostgreSQL database hosted on RDS.  
- **Load Balancer:** Application Load Balancer (ALB) for routing traffic between services.  
- **API Gateway:** Facilitates secure and efficient backend communication.  
- **AWS Secrets Manager:** Secure storage for sensitive data such as database credentials.  

### Diagram  
![Architecture Diagram](./screenshots/architecture-diagram.png)  

---

## Tech Stack  

| Component         | Technology                |  
|-------------------|---------------------------|  
| **Frontend**      | HTML, CSS, JavaScript, Nginx |  
| **Backend**       | Spring Boot, Java         |  
| **Database**      | PostgreSQL (AWS RDS)      |  
| **DevOps**        | Docker, AWS (EC2, ECR, API Gateway, Load Balancer, RDS, Secrets Manager) |  
| **Authentication**| AWS Cognito               |  
| **Version Control**| GitHub                   |  
| **Project Management**| Jira                  |  

---

## Development Workflow  

- **Branching Strategy**  
  - Feature branches (`EIT-XX-US-Name`) for individual user stories.  
  - Pull requests reviewed and merged into the `dev` branch.  
  - The `main` branch used for production-ready code.  

- **Tools**  
  - GitHub for version control.  
  - Jira for project management, sprint tracking, and burndown charts.  

---

## Agile Methodology  

The project followed Agile practices, structured into multiple sprints.  
Each sprint delivered incremental features, with user stories adhering to the following principles:  

- **User Stories:**  
  - Written in Jira with clear acceptance criteria and priorities.  
  - Associated with Epics for high-level categorization.  
  - Accessible at [Epics](./docs/Epics/)  

- **Sprints:**  
  - Sprint reviews and retrospectives were conducted to assess progress and challenges.  
  - Detailed documentation of goals, completed work, and future improvements.  
  - Accessible at [Sprints](./docs/Sprints/)  

---

## Deployment  

- **Frontend:** Hosted on AWS EC2, accessible via Nginx.  
- **Backend:** Spring Boot application deployed via Docker and AWS EC2.  
- **Database:** AWS RDS ensures reliability and scalability.  
- **Load Balancer:** Routes traffic and enforces HTTPS using SSL certificates.  
- **API Gateway:** Manages secure backend communication with CORS configured.  

---

## Definitions  

### Definition of Ready  

| Definition of Ready |  
|:--------------------:|  
| Formatted as "As a (...), I want (...) so that (...)" |  
| Short and self-explanatory |  
| Defined story point |  
| Acceptance criteria as "Given (...) when (...) then (...)" |  
| INVEST methodology |  
| Estimated priority, given by the position in the backlog |  

### Definition of Done  

| Definition of Done |  
|:-------------------:|  
| Developed |  
| Documented |  
| Compliant with Acceptance Criteria |  
| Merged into central branch ("dev") |  

---

## Author  
[Miguel Miragaia]  
[Universidade de Aveiro - DETI]  
[13/12/2024]  
