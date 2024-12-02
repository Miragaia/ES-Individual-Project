const COGNITO_DOMAIN = "https://us-east-1fmicepjif.auth.us-east-1.amazoncognito.com";
const CLIENT_ID = "39hvrchbdl9i7nf6t0dgnldlj4";
const REDIRECT_URI = "http://localhost:3000/index.html";

document.getElementById("logoutBtn").addEventListener("click", () => {
    console.log("Logging out...");
    // Construct the Cognito logout URL
    const logoutUrl = `https://us-east-1fmicepjif.auth.us-east-1.amazoncognito.com/logout?client_id=39hvrchbdl9i7nf6t0dgnldlj4&logout_uri=http://localhost:3000/index.html`;

    // Clear local storage
    localStorage.removeItem("token");

    // Redirect to the Cognito logout URL
    window.location.href = logoutUrl;
});
