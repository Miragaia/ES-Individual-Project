# EIT-4 Task Visibility

## Summary
As a logged-in user, I want to create a task that is only visible to me so that I can maintain the security and privacy of my tasks.

## User Story
- **Title**: Task Visibility
- **Epic**: Task Ownership
- **Description**: Ensures that tasks created by a user are only visible to that user, maintaining privacy and security.

## Acceptance Criteria
- **Given** I am a logged-in user
- **When** I access my tasks on the dashboard page
- **Then** I can only see my own tasks.

## Implementation Details
Each task created is associated with the user’s unique ID. Only tasks linked to the logged-in user’s ID are displayed on their dashboard, ensuring private access.

## Screenshots
![Task Visibility](./screenshots/task-visibility.png)

## Related Links
- [Parent Epic: Task Ownership](../README.md)
