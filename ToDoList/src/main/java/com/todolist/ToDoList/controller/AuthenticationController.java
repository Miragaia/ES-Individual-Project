package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.DTO.CreateUserRequest;
import com.todolist.ToDoList.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.todolist.ToDoList.service.UserService;
import com.todolist.ToDoList.service.JwtService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Public API for managing user authentication")
public class AuthenticationController {

    @Value("${spring.security.oauth2.client.registration.cognito.clientId}")
    private String clientId; // Your Cognito app client ID

    @Value("${spring.security.oauth2.client.registration.cognito.clientSecret}")
    private String clientSecret; // Your Cognito app client secret

    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String redirectUri; // The redirect URI you configured in Cognito

    @Value("${spring.security.oauth2.client.provider.cognito.token-uri}")
    private String tokenEndpoint; // The Cognito token endpoint

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    public AuthenticationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/exchange")
    public ResponseEntity<Map<String, String>> exchangeCodeForTokens(
        @RequestParam("code") String code,
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("redirect_uri") String redirectUri) {

        logger.info("Code: " + code);
        
        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code is required"));
        }

        // Prepare the request body for exchanging the code for tokens using MultiValueMap
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        // Create the headers (Cognito expects application/x-www-form-urlencoded content type)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Build the form-encoded data
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenEndpoint);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Make the POST request to exchange the code for tokens
            ResponseEntity<Map> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            // Extract tokens from the response body
            Map<String, String> responseBody = response.getBody();
            if (responseBody != null) {
                String accessToken = responseBody.get("access_token");
                String refreshToken = responseBody.get("refresh_token");
                String idToken = responseBody.get("id_token");
                
                System.out.println("Access Token: " + accessToken);
                System.out.println("Refresh Token: " + refreshToken);
                System.out.println("ID Token: " + idToken);

                // Decode the idToken to extract claims
                DecodedJWT decodedJWT = JWT.decode(idToken);
                String cognitoEmail= decodedJWT.getClaim("cognito:email").asString();

                System.out.println("Cognito Email: " + cognitoEmail);

                // Return the tokens in the response
                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                tokens.put("cognitoEmail", cognitoEmail);


                // Add CORS headers to the response
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("Access-Control-Allow-Origin", "*"); // Allow the frontend to access the response
                responseHeaders.add("Access-Control-Allow-Credentials", "true"); // Allow cookies and credentials in cross-origin requests      (maybe tirar)

                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve tokens"));
            }

        } catch (Exception e) {
            // Handle any errors during the exchange
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}