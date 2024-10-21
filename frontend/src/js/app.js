document.addEventListener('DOMContentLoaded', function () {
    const taskTableBody = document.querySelector("#taskTable tbody");
    const taskModal = document.getElementById("taskModal");
    const openTaskModalBtn = document.getElementById("openTaskModalBtn");
    const closeTaskModalBtn = document.getElementById("closeTaskModalBtn");
    const addTaskBtn = document.getElementById("addTaskBtn");

    // Base URL for API requests
    const API_URL = "http://localhost:8080/api/tasks";
    
    // JWT token (to be replaced with a real authentication method)
    const token = localStorage.getItem('token');  // Assuming you store the token in localStorage after login
    console.log('Token:', token);

    // Set up headers with Authorization token
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    console.log('Headers:', headers);

    // Fetch tasks from the server
    function fetchTasks() {
        fetch(API_URL, {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            renderTasks(data);
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    // Render tasks in the table
    function renderTasks(tasks) {
        taskTableBody.innerHTML = ''; // Clear the current table rows

        tasks.forEach((task) => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${task.title}</td>
                <td>${task.description}</td>
                <td>${task.priority}</td>
                <td>${new Date(task.deadline).toLocaleString()}</td>
                <td>
                    <button onclick="deleteTask('${task.id}')">Delete</button>
                </td>
            `;

            taskTableBody.appendChild(row);
        });
    }

    // Add a new task
    addTaskBtn.addEventListener("click", function () {
        const title = document.getElementById("taskTitle").value;
        const description = document.getElementById("taskDescription").value;
        const deadline = document.getElementById("taskDeadline").value;
        const priority = document.getElementById("taskPriority").value;
    
        const newTask = {
            title: title,
            description: description,
            deadline: deadline,
            priority: priority
        };

        // Get the JWT token
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("No JWT token found. User is not logged in.");
            return;
        }
    
        fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`  // Attach the token to the request
            },
            body: JSON.stringify(newTask)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            // Check if the response has content before parsing
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            console.log("Task added successfully:", data);
            // Re-fetch tasks to update the table with the newly added task
            fetchTasks();
            closeTaskModal();  // Close the modal
        })
        .catch(error => console.error('Error adding task:', error));
    });

    // Delete a task
    window.deleteTask = function (taskId) {
        fetch(`${API_URL}/${taskId}`, {
            method: 'DELETE',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                // Re-fetch tasks to update the table after deletion
                fetchTasks();
            } else {
                console.error('Error deleting task:', response.statusText);
            }
        })
        .catch(error => console.error('Error deleting task:', error));
    };

    // Open the modal for task creation
    openTaskModalBtn.addEventListener("click", function () {
        taskModal.style.display = "block";
    });

    // Close the modal
    closeTaskModalBtn.addEventListener("click", function () {
        closeTaskModal();
    });

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        if (event.target === taskModal) {
            closeTaskModal();
        }
    };

    // Close task modal function
    function closeTaskModal() {
        taskModal.style.display = "none";

        // Clear form inputs
        document.getElementById("taskTitle").value = '';
        document.getElementById("taskDescription").value = '';
        document.getElementById("taskDeadline").value = '';
        document.getElementById("taskPriority").value = 'LOW';
    }

    // Fetch and render tasks when the page loads
    fetchTasks();
});
