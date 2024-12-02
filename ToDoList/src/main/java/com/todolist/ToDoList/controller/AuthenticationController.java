package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Public API for managing user authentication")
public class AuthenticationController {

    @Value("${spring.security.oauth2.client.registration.cognito.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.cognito.clientSecret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.cognito.token-uri}")
    private String tokenEndpoint;

    private final RestTemplate restTemplate;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    public AuthenticationController(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
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

                logger.info("Access Token: " + accessToken);
                logger.info("Refresh Token: " + refreshToken);

                // Decode the idToken to extract claims
                DecodedJWT decodedJWT = JWT.decode(idToken);
                String userSub = decodedJWT.getClaim("sub").asString();


                // Check if the user already exists
                User user = userService.getUserBySub(userSub);

                if (user == null) {
                    // If the user does not exist, create a new user
                    User newUser = new User();
                    newUser.setUserSub(userSub);
                    userService.createUser(newUser);
                    logger.info("Created new user: " + newUser);
                }

                // Return the tokens in the response
                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                tokens.put("cognitoEmail", userSub);


                // Add CORS headers to the response
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("Access-Control-Allow-Origin", "*");
                responseHeaders.add("Access-Control-Allow-Credentials", "true");

                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve tokens"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}