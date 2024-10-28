document.addEventListener('DOMContentLoaded', function () {
    const registerForm = document.getElementById('registerForm');
    const loginForm = document.getElementById('loginForm');
    const messageDiv = document.getElementById('message');

    // Handle registration
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: username,
                        email: email,
                        password: password
                    })
                });

                if (response.ok) {
                    messageDiv.textContent = 'Registration successful! You can now login.';
                    messageDiv.style.color = 'green';
                } else if (response.status === 409) {
                    messageDiv.textContent = 'Username or Email already exists!';
                    messageDiv.style.color = 'red';
                } else {
                    messageDiv.textContent = 'Failed to register. Please try again.';
                    messageDiv.style.color = 'red';
                }
            } catch (error) {
                messageDiv.textContent = 'Error occurred during registration.';
                messageDiv.style.color = 'red';
            }
        });
    }

    // Handle login
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: new URLSearchParams({
                        email: email,
                        password: password
                    })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('token', data.token);  // Store JWT token
                    messageDiv.textContent = 'Login successful!';
                    messageDiv.style.color = 'green';
                    
                    // Redirect to task page or other protected page
                    window.location.href = 'tasks.html';
                } else {
                    messageDiv.textContent = 'Invalid email or password!';
                    messageDiv.style.color = 'red';
                }
            } catch (error) {
                messageDiv.textContent = 'Error occurred during login.';
                messageDiv.style.color = 'red';
            }
        });
    }
});
