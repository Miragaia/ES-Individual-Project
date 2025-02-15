const COGNITO_DOMAIN = "https://us-east-1kna5gmgve.auth.us-east-1.amazoncognito.com";
const CLIENT_ID = "cvdqu3l4h9ntg0e895a48eogv";
const REDIRECT_URI = "https://es-ua.ddns.net/index.html";

document.getElementById("logoutBtn").addEventListener("click", () => {
    console.log("Logging out...");
    // Construct the Cognito logout URL
    const logoutUrl = `https://us-east-1kna5gmgve.auth.us-east-1.amazoncognito.com/logout?client_id=cvdqu3l4h9ntg0e895a48eogv&logout_uri=https://es-ua.ddns.net/index.html`;

    // Clear local storage
    localStorage.removeItem("token");

    // Redirect to the Cognito logout URL
    window.location.href = logoutUrl;
});
