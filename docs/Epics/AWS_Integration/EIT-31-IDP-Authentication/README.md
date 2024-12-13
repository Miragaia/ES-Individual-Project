# EIT-31 IDP Authentication

## Summary
As a user, I want to be able to login/register using Cognito IDP so that I can authenticate into the application and be redirected to my task dashboard page.

## User Story
- **Title**: User Registration
- **Epic**: Task Ownership
- **Description**: Allows new users to create an account in order to access and use the to-do list application.

## Acceptance Criteria
- **Given** I am at the index page
- **When** I click “Login”
- **Then** I am taken to the Cognito UI IDP
- **When** I authenticate/register with my account credentials
- **Then** I am taken to the system’s dashboard

## Implementation Details
The authentication page is the pre built UI of Cognito IDP, the passwords strength, and the other necessities are all controlled by Cognito.

Upon successful registration, the user can immediately access their task dashboard.

## Screenshots
![Registration Page](./screenshots/registration-page.png)

## Related Links
- [Parent Epic: AWS Integraton](../README.md)