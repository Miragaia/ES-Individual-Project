const apiUrl = "http://localhost:8080/api/tasks"; // Your Spring Boot backend API URL

// Function to fetch tasks from the backend
async function fetchTasks() {
    try {
        const response = await fetch(apiUrl); // GET request to fetch all tasks
        const data = await response.json();
        tasks = data; // Assign response to tasks array
        renderTasks(); // Render the tasks in the table
    } catch (error) {
        console.error("Error fetching tasks:", error);
    }
}

// Function to render tasks in a table
function renderTasks() {
    const taskTableBody = document.querySelector("#taskTable tbody");
    taskTableBody.innerHTML = ''; // Clear the current table rows

    tasks.forEach((task, index) => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${task.title}</td>
            <td>${task.description}</td>
            <td>${task.priority}</td>
            <td>${task.deadline}</td>
            <td><button onclick="deleteTask(${task.id})">Delete</button></td>
        `;

        taskTableBody.appendChild(row);
    });
}

// Function to add a new task to the backend
async function addTaskToBackend(task) {
    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(task) // Send the task data as JSON
        });
        if (response.ok) {
            fetchTasks(); // Re-fetch tasks after adding a new one
        } else {
            console.error("Failed to add task");
        }
    } catch (error) {
        console.error("Error adding task:", error);
    }
}

// Function to delete a task from the backend
async function deleteTask(id) {
    try {
        const response = await fetch(`${apiUrl}/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            fetchTasks(); // Re-fetch tasks after deletion
        } else {
            console.error("Failed to delete task");
        }
    } catch (error) {
        console.error("Error deleting task:", error);
    }
}

// Function to add a new task
document.getElementById("addTaskBtn").addEventListener("click", () => {
    const title = document.getElementById("taskTitle").value;
    const description = document.getElementById("taskDescription").value;
    const deadline = document.getElementById("taskDeadline").value;
    const priority = document.getElementById("taskPriority").value;

    const task = {
        title: title,
        description: description,
        deadline: deadline,
        priority: priority
    };

    addTaskToBackend(task); // Send task to backend
    closeTaskModal(); // Close the modal after adding the task
});

// Modal functionality
const taskModal = document.getElementById("taskModal");
const openTaskModalBtn = document.getElementById("openTaskModalBtn");
const closeTaskModalBtn = document.getElementById("closeTaskModalBtn");

// Open the modal
openTaskModalBtn.addEventListener("click", () => {
    taskModal.style.display = "block";
});

// Close the modal
closeTaskModalBtn.addEventListener("click", () => {
    closeTaskModal();
});

// Close modal on clicking outside of it
window.onclick = function(event) {
    if (event.target === taskModal) {
        closeTaskModal();
    }
};

// Function to close the modal
function closeTaskModal() {
    taskModal.style.display = "none";
}

// Initialize tasks when the page loads
window.onload = fetchTasks;
