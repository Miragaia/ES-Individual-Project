# EIT-3 User Login

## Summary
As a registered user, I want to sign in to my account so that I can access and manage my tasks securely.

## User Story
- **Title**: User Login
- **Epic**: Task Ownership
- **Description**: Allows registered users to log in to their accounts and access the app’s dashboard, providing secure access to their tasks.

## Acceptance Criteria
- **Given** I am a registered user on the login page
- **When** I fill in my user credentials
- **Then** I am taken to the app dashboard.

## Implementation Details
The login page includes a form where users enter their:
  - Username or email
  - Password

Upon successful login, the user’s credentials are verified. If the login is successful, they are redirected to the app's main dashboard, where they can manage their tasks. If the credentials are incorrect, an error message is displayed.

## Screenshots
![Login Page](./screenshots/login-page.png)

## Related Links
- [Parent Epic: Task Ownership](../README.md)
