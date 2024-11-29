# EIT-6 Create Task

## Summary
As a logged-in user, I want to create new tasks with a title, description, deadline, category, and priority, so that I can organize my tasks and track what needs to be done.

## User Story
- **Title**: Create Task
- **Epic**: Task Management
- **Description**: Allows users to create new tasks by providing essential details.

## Acceptance Criteria
- **Given** I am a logged-in user on the task creation page
- **When** I fill out the task details and click “Add Task”
- **Then** a new task should be created and displayed in the task list.

## Implementation Details
The task creation page includes a form where users can input the following:
  - Title
  - Description 
  - Deadline
  - Priority (Low, Medium, High)
Category will be added to the Task after creation.

When the user submits the form, the task is validated and saved to the database. The newly created task then appears in the main task list.

## Screenshots
![Task Creation Form](./screenshots/create-task-form.png)

## Related Links
- [Parent Epic: Task Management](../README.md)
