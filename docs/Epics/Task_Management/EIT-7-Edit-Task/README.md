# EIT-7 Edit Task

## Summary
As a user with existing tasks, I want to edit my tasks so that I can update task details if necessary.

## User Story
- **Title**: Edit Task
- **Epic**: Task Management
- **Description**: Enables users to modify details of an existing task.

## Acceptance Criteria
- **Given** I am a logged-in user on the task dashboard page
- **When** I click the edit button of a task and change some details
- **Then** the task should be displayed according to the modifications.

## Implementation Details
Users can edit tasks by selecting the "Edit" option on any existing task. The edit form is pre-populated with the current details of the selected task, allowing changes to:
  - Title
  - Description
  - Deadline
  - Category
  - Priority
  - Status

Upon submission, the modified details are saved to the database, and the task list updates to reflect the changes.

## Screenshots
![Edit Task Form](./screenshots/edit-task-form.png)

## Related Links
- [Parent Epic: Task Management](../README.md)
