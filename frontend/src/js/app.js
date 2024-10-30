let tasksData = [];  // Global variable to store tasks

document.addEventListener('DOMContentLoaded', function () {
    const taskTableBody = document.querySelector("#taskTable tbody");
    const taskModal = document.getElementById("taskModal");
    const openTaskModalBtn = document.getElementById("openTaskModalBtn");
    const taskModalEdit = document.getElementById("taskModalEdit");
    const openCategoryModalBtn = document.getElementById("openCategoryModalBtn");
    const closeTaskModalBtnEdit = document.getElementById("closeTaskModalBtnEdit");
    const closeTaskModalBtn = document.getElementById("closeTaskModalBtn");
    const closeCatModalBtn = document.getElementById("closeCatModalBtn");
    const addTaskBtn = document.getElementById("addTaskBtn");
    const categoryModal = document.getElementById("catModal");  // For adding or creating categories
    const addCatBtn = document.getElementById("addCatBtn");

    // Base URL for API requests
    const API_URL = "http://localhost:8080/api/tasks";
    const CATEGORY_URL = "http://localhost:8080/api/categories";
    
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
            tasksData = data;  // Store tasks globally
            renderTasks(tasksData);
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    // Render tasks in the table
    function renderTasks(tasks) {
        taskTableBody.innerHTML = ''; // Clear the current table rows

        tasks.forEach((task) => {
            const row = document.createElement('tr');

            // Check if the category exists
            const category = task.category ? task.category.title : `<button onclick="openCategoryModal('${task.id}')">Add Category</button>`;

            row.innerHTML = `
                <td>${task.title}</td>
                <td>${task.description}</td>
                <td>${task.priority}</td>
                <td>${task.deadline.toLocaleString()}</td>
                <td>${task.status}</td>
                <td>${category}</td>
                <td>
                    <button class="action-btn edit-btn" onclick="openTaskModalEdit('${task.id}')">
                        <i class="fa fa-pencil"></i>
                    </button>
                    <button class="action-btn complete-btn" onclick="completeTask('${task.id}')">
                        <i class="fa fa-check"></i>
                    </button>
                    <button class="action-btn delete-btn" onclick="deleteTask('${task.id}')">
                        <i class="fa fa-times"></i>
                    </button>
                </td>
            `;

            taskTableBody.appendChild(row);
        });
    }

   // Open the task modal for editing and populate it
    window.openTaskModalEdit = function (taskId) {
        const task = tasksData.find(t => t.id === taskId); // Find the task by its ID
        if (!task) return;

        // Populate the modal with task details
        document.getElementById("taskTitleEdit").value = task.title;
        document.getElementById("taskDescriptionEdit").value = task.description;
        document.getElementById("taskDeadlineEdit").value = task.deadline.split('T')[0]; // Convert to yyyy-mm-dd format
        document.getElementById("taskPriorityEdit").value = task.priority;
        document.getElementById("taskStatus").value = task.status;

        // Open the edit modal
        taskModalEdit.style.display = "block";

        // Modify the existing task when saving
        document.getElementById("editTaskBtn").onclick = () => {
            // Populate task object with updated values from the modal
            task.title = document.getElementById("taskTitleEdit").value;
            task.description = document.getElementById("taskDescriptionEdit").value;
            task.deadline = document.getElementById("taskDeadlineEdit").value;  
            task.priority = document.getElementById("taskPriorityEdit").value;
            task.status = document.getElementById("taskStatus").value;

            // Today's date in mm-dd-yyyy format
            const now = new Date().toISOString().split("T")[0].split("-").join("-");

            // Ensure no fields are empty and deadline is a future date
            if (!task.title || !task.description || !task.deadline || !task.priority || !task.status) {
                alert("All fields must be filled out.");
                return;
            }
            if (task.deadline <= now) {
                alert("The deadline must be a future date.");
                return;
            }

            // Send updated task to server
            fetch(`${API_URL}/${taskId}`, {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify(task)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Task updated successfully:", data);
                closeTaskModalEdit();
                fetchTasks(); // Re-fetch tasks after update
            })
            .catch(error => console.error('Error updating task:', error));
        };

    }


    // Function to complete a task
    window.completeTask = function(taskId) {
        const task = tasksData.find(t => t.id === taskId); // Find the task by its ID
        if (!task) return;

        task.status = "COMPLETED"; // Update the task status

        // Send status update to server
        fetch(`${API_URL}/${taskId}`, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(task)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Task completed successfully:", data);
            fetchTasks(); // Re-fetch tasks after marking as complete
        })
        .catch(error => console.error('Error completing task:', error));
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

        console.log("New task:", newTask);

        // Get the JWT token
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("No JWT token found. User is not logged in.");
            return;
        }

        // Today's date in mm-dd-yyyy format
        const now = new Date().toISOString().split("T")[0].split("-").join("-");

        // Ensure no fields are empty and deadline is a future date
        if (!newTask.title || !newTask.description || !newTask.deadline || !newTask.priority) {
            alert("All fields must be filled out.");
            return;
        }
        if (newTask.deadline <= now) {
            alert("The deadline must be a future date.");
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
            fetchTasks(); // Re-fetch tasks after adding
            closeTaskModal();  // Close the modal
        })
        .catch(error => console.error('Error adding task:', error));
    });



    // Add a new Category
    addCatBtn.addEventListener("click", function () {
        const title = document.getElementById("catTitle").value;
        const description = document.getElementById("catDescription").value;
    
        const newCat = {
            title: title,
            description: description,
        };

        console.log("New Category:", newCat);

        // Get the JWT token
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("No JWT token found. User is not logged in.");
            return;
        }

        // Ensure no fields are empty and deadline is a future date
        if (!newCat.title || !newCat.description) {
            alert("All fields must be filled out.");
            return;
        }

        fetch(CATEGORY_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`  // Attach the token to the request
            },
            body: JSON.stringify(newCat)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            // Check if the response has content before parsing
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            console.log("Category added successfully:", data);
            fetchTasks(); // Re-fetch tasks after adding
            closeCatModal();  // Close the modal
        })
        .catch(error => console.error('Error adding Category:', error));
    });




    // Delete a task
    window.deleteTask = function (taskId) {
        fetch(`${API_URL}/${taskId}`, {
            method: 'DELETE',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                fetchTasks(); // Re-fetch tasks after deletion
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

    // Close modal for editing
    closeTaskModalBtnEdit.addEventListener("click", function () {
        closeTaskModalEdit();
    });

    openCategoryModalBtn.addEventListener("click", function () {
        categoryModal.style.display = "block";
    });

    closeCatModalBtn.addEventListener("click", function () {
        closeCatModal();
    });

    // Close task modal edit function
    function closeTaskModalEdit() {
        taskModalEdit.style.display = "none";

        // Clear form inputs
        document.getElementById("taskTitleEdit").value = '';
        document.getElementById("taskDescriptionEdit").value = '';
        document.getElementById("taskDeadlineEdit").value = '';
        document.getElementById("taskPriorityEdit").value = 'LOW';
    }

    // Close the modal
    closeTaskModalBtn.addEventListener("click", function () {
        closeTaskModal();
    });

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        if (event.target === taskModalEdit) {
            closeTaskModalEdit();
        }
        if (event.target === taskModal) {
            closeTaskModal();
        }
        if (event.target === categoryModal) {
            closeCatModal();
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

    function closeCatModal() {
        categoryModal.style.display = "none";

        // Clear form inputs
        document.getElementById("catTitle").value = '';
        document.getElementById("catDescription").value = '';

    }

    // Fetch and render tasks when the page loads
    fetchTasks();
});
