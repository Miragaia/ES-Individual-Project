package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

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

    @Operation(summary = "Exchange authorization code for tokens",
            description = "Exchanges a Cognito authorization code for access, refresh, and ID tokens.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens exchanged successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"accessToken\":\"<access_token>\", \"refreshToken\":\"<refresh_token>\", \"cognitoEmail\":\"<user_sub>\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request or missing parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Code is required\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Failed to retrieve tokens\"}")))
    })
    @PostMapping("/exchange")
    public ResponseEntity<Map<String, String>> exchangeCodeForTokens(
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri) {

        logger.info("Authorization Code: {}", code);

        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code is required"));
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenEndpoint);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, String> responseBody = response.getBody();
            if (responseBody != null) {
                String accessToken = responseBody.get("access_token");
                String refreshToken = responseBody.get("refresh_token");
                String idToken = responseBody.get("id_token");

                logger.info("Access Token: {}", accessToken);
                logger.info("Refresh Token: {}", refreshToken);

                DecodedJWT decodedJWT = JWT.decode(idToken);
                String userSub = decodedJWT.getClaim("sub").asString();

                User user = userService.getUserBySub(userSub);
                if (user == null) {
                    User newUser = new User();
                    newUser.setUserSub(userSub);
                    userService.createUser(newUser);
                    logger.info("Created new user: {}", newUser);
                }

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                tokens.put("cognitoEmail", userSub);
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("Access-Control-Allow-Origin", "*");
                responseHeaders.add("Access-Control-Allow-Credentials", "true");
                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve tokens"));
            }
        } catch (Exception e) {
            logger.error("Error during token exchange: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
