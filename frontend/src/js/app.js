let tasksData = [];

document.addEventListener('DOMContentLoaded', function () {
    const taskTableBody = document.querySelector("#taskTable tbody");

    //Task Creation Modal
    const taskModal = document.getElementById("taskModal");
    const openTaskModalBtn = document.getElementById("openTaskModalBtn");
    const closeTaskModalBtn = document.getElementById("closeTaskModalBtn");
    const addTaskBtn = document.getElementById("addTaskBtn");

    //Task Edit Modal
    const taskModalEdit = document.getElementById("taskModalEdit");
    const openCategoryModalBtn = document.getElementById("openCategoryModalBtn");
    const closeTaskModalBtnEdit = document.getElementById("closeTaskModalBtnEdit");

    const closeCatModalBtn = document.getElementById("closeCatModalBtn");

    const categoryModal = document.getElementById("catModal");  // For adding or creating categories
    const addCatBtn = document.getElementById("addCatBtn");

    const viewCategoriesBtn = document.getElementById("viewCategoriesBtn");
    const viewCategoriesModal = document.getElementById("viewCategoriesModal");
    const closeViewCategoriesModalBtn = document.getElementById("closeViewCategoriesModalBtn");
    const categoriesList = document.getElementById("categoriesList");


    //stay at app.js
    const assignCategoryModal = document.getElementById("assignCategoryModal");
    const closeAssignCategoryModalBtn = document.getElementById("closeAssignCategoryModalBtn");
    const categorySelect = document.getElementById("categorySelect");
    let categoriesAvailable = false;


    const assignCategoryBtn = document.getElementById("assignCategoryBtn");

    let selectedTaskId = null; // Store task ID for assigning category


    const categoryFilter = document.getElementById("category"); // The category filter dropdown 

    const statusFilter = document.getElementById("status");

    const sortBySelect = document.getElementById("sortBy");

    // Add event listeners to filter by both status and category
    statusFilter.addEventListener("change", fetchFilteredAndSortedTasks);
    categoryFilter.addEventListener("change", fetchFilteredAndSortedTasks);
    sortBySelect.addEventListener("change", fetchFilteredAndSortedTasks);

    // TASKS and CATEGORIES API URLs
    const API_URL = "http://localhost:8080/api/tasks";
    const CATEGORY_URL = "http://localhost:8080/api/categories";
    
    // JWT token Storage
    const token = localStorage.getItem('token');
    console.log('Token:', token);

    // Headers with Authorization token
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    function fetchFilteredAndSortedTasks() {
        const status = statusFilter.value !== "ALL" ? statusFilter.value : null;
        const categoryId = categoryFilter.value !== "all" ? categoryFilter.value : null;
        const sortBy = sortBySelect.value;

        // Construct URL with query parameters
        let url = `${API_URL}/filter?sortBy=${sortBy}`;
        if (status) url += `&status=${status}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        
        fetch(url, {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            renderTasks(data); // Render tasks based on the fetched data
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    // Fetch tasks (will be reused)
    function fetchTasks(categoryId = null) {
        let url = categoryId ? `${API_URL}/filterCategory?categoryId=${categoryId}` : API_URL;
        
        fetch(url, {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            tasksData = data;
            renderTasks(tasksData);
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    // Assign Category modal (in Tasks Table)   
    window.openAssignCategoryModal = function (taskId) {
        selectedTaskId = taskId;
        fetchUserCategoriesDropdown();  //needs to stay at app.js
        assignCategoryModal.style.display = "block";    //needs to stay at app.js
    };

    // Enable/Disable Add Category button (needs to stay at app.js)
    function toggleAddCategoryButtons(enable) {
        const buttons = document.querySelectorAll('.add-category-btn');
        buttons.forEach(button => {
            button.disabled = !enable;
            if (!enable) {
                button.textContent = "Not Available";
            } else {
                button.textContent = "Add Category";
            }
        });
    }

    // Fetch categories for the Assign Category dropdown (needs to stay at app.js)
    function fetchUserCategoriesDropdown() {
        fetch(CATEGORY_URL + "/user", {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(categories => {
            
            categorySelect.innerHTML = "";
            categoryFilter.innerHTML = `<option value="all">All</option>`;
            
            
            if (categories.length === 0) {
                categoriesAvailable = false;
                toggleAddCategoryButtons(false);

                const noCategoryOption = document.createElement("option");
                noCategoryOption.disabled = true;
                noCategoryOption.textContent = "No categories available";
                categoryFilter.appendChild(noCategoryOption);

            } else {
                categoriesAvailable = true;
                toggleAddCategoryButtons(true);

                categories.forEach(category => {
                    const option = document.createElement("option");
                    option.value = category.id;
                    option.textContent = category.title;
                    categorySelect.appendChild(option);
                });

                categories.forEach(category => {
                    const option = document.createElement("option");
                    option.value = category.id;
                    option.text = category.title;
                    categoryFilter.appendChild(option);
                });
            }
        })
        .catch(error => console.error("Error fetching user categories:", error));
    }

    // Render tasks in the table
    function renderTasks(tasks) {
        taskTableBody.innerHTML = ''; // Clear the current table rows

        tasks.forEach((task) => {
            const row = document.createElement('tr');

            // Check if the category exists
            const category = task.category 
                ? task.category.title 
                : `<button id="addCategoryButton-${task.id}" class="add-category-btn" onclick="openAssignCategoryModal('${task.id}')">Add Category</button>`;

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

        // After rendering tasks, update button state based on category availability
        toggleAddCategoryButtons(categoriesAvailable);
    }
        

    // Event listener for the "Assign Category" button in the modal
    assignCategoryBtn.addEventListener("click", function () {
        const categoryId = categorySelect.value;
        if (!categoryId || !selectedTaskId) return;

        fetch(`${CATEGORY_URL}/${selectedTaskId}/category/${categoryId}`, {
            method: 'PUT', // Use PUT as specified
            headers: headers
        })
        .then(response => {
            if (!response.ok) throw new Error(`Error: ${response.statusText}`);
            console.log(`Category ${categoryId} assigned to task ${selectedTaskId}`);
            assignCategoryModal.style.display = "none"; // Close the modal
            fetchTasks(); // Refresh tasks to show updated category
        })
        .catch(error => console.error("Error assigning category:", error));
    });

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        if (event.target === assignCategoryModal) {
            assignCategoryModal.style.display = "none";
        }
    };

    // // Event listener for the status filter dropdown
    // statusFilter.addEventListener("change", function () {
    //     const selectedStatus = statusFilter.value;
    //     fetchTasksByStatus(selectedStatus);
    // }); 

    // Close the Assign Category modal
    closeAssignCategoryModalBtn.addEventListener("click", function () {
        assignCategoryModal.style.display = "none";
    });

    function populateCategoryDropdownForEdit(task) {
        const categoryDropdown = document.getElementById("taskCategoryEdit");
    
        // Clear existing options
        categoryDropdown.innerHTML = '';
    
        // Add the "None" option at the top
        const noneOption = document.createElement("option");
        noneOption.value = '';
        noneOption.text = 'None';
        categoryDropdown.appendChild(noneOption);
    
        // Fetch categories from the backend
        fetch(`${CATEGORY_URL}/user`, {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(categories => {
            categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.id;
                option.text = category.title;
                categoryDropdown.appendChild(option);
            });
    
            // If the task has a category, set it as the selected option; otherwise, select "None"
            categoryDropdown.value = task.category ? task.category.id : '';
        })
        .catch(error => console.error('Error fetching categories:', error));
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

        populateCategoryDropdownForEdit(task);

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

            // Get the selected category ID
            const selectedCategoryId = document.getElementById("taskCategoryEdit").value;
            task.category = selectedCategoryId ? { id: selectedCategoryId } : null;

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
                fetchUserCategoriesDropdown();
                fetchTasks(); // Re-fetch tasks after update
                fetchTasksByStatus("ALL");
                document.getElementById("status").value = "ALL";
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
            fetchUserCategoriesDropdown(); // Re-fetch categories for the filter dropdown
            fetchTasksByStatus("ALL");
            document.getElementById("status").value = "ALL";
        })
        .catch(error => console.error('Error completing task:', error));
    }


    // Function to fetch categories from the server
    function fetchCategories() {
        fetch(`${CATEGORY_URL}/user`, {   // Fetch categories for the logged-in user
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(categories => renderCategories(categories))
        .catch(error => console.error('Error fetching categories:', error));
    }

    // Render categories in the list
    function renderCategories(categories) {
        categoriesList.innerHTML = ''; // Clear current list
        categories.forEach(category => {
            const listItem = document.createElement('li');
            listItem.textContent = `${category.title}: ${category.description}`;
            categoriesList.appendChild(listItem);
        });
    }

    // Open the modal for viewing categories
    viewCategoriesBtn.addEventListener("click", function () {
        if (!categoriesAvailable) {
            alert("No categories found. Please add a category first.");
            return;
        }
        fetchCategories();  // Fetch and render categories
        viewCategoriesModal.style.display = "block";
    });

    // Close the modal for viewing categories
    closeViewCategoriesModalBtn.addEventListener("click", function () {
        viewCategoriesModal.style.display = "none";
    });

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        if (event.target === viewCategoriesModal) {
            viewCategoriesModal.style.display = "none";
        }
    };



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
            fetchUserCategoriesDropdown(); // Re-fetch categories after adding
            fetchTasksByStatus("ALL");
            document.getElementById("status").value = "ALL";
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
        if (event.target === viewCategoriesModal) {
            viewCategoriesModal.style.display = "none";
        }
        if (event.target === assignCategoryModal) {
            assignCategoryModal.style.display = "none";
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


    // Function to filter tasks by category
    function filterTasksByCategory(categoryId) {
        fetch(`${API_URL}/filterCategory?categoryId=${categoryId}`, {
            method: 'GET',
            headers: headers
        })
        .then(response => {
            if (!response.ok) throw new Error(`Error: ${response.statusText}`);
            return response.json();
        })
        .then(tasks => {
            renderTasks(tasks); // Use existing render function to display filtered tasks
        })
        .catch(error => console.error('Error fetching tasks by category:', error));
    }

    // Fetch and display tasks based on the selected status
    function fetchTasksByStatus(status) {
        let url = `${API_URL}/filterByStatus?status=${status}`;
        if (status === "ALL") {
            url = API_URL; // Get all tasks if "ALL" is selected
        }

        fetch(url, {
            method: 'GET',
            headers: headers
        })
        .then(response => response.json())
        .then(data => {
            renderTasks(data); // Render tasks based on the fetched data
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    fetchUserCategoriesDropdown(); // Fetch user categories when the page loads

    fetchTasksByStatus("ALL");
    document.getElementById("status").value = "ALL";

    fetchFilteredAndSortedTasks();

    // Fetch and render tasks when the page loads
    fetchTasks();
});
