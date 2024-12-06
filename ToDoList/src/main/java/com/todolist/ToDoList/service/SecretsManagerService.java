package com.todolist.ToDoList.service;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper;

    // @Value("${aws.region:us-east-1}") // Allow the region to be injected via properties file, default to us-east-1
    // private String awsRegion;

    public SecretsManagerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.of("us-east-1"))  // Dynamically set the region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public Map<String, String> getSecrets(String secretName) {
        try {
            // Create the request to fetch the secret
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            // Fetch the secret value
            GetSecretValueResponse getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

            // The secret could be a string, so we need to handle that
            String secretString = getSecretValueResponse.secretString();
            if (secretString != null) {
                // Parse the secret string into a Map
                return objectMapper.readValue(secretString, Map.class);
            } else {
                throw new RuntimeException("Secret value is empty for secret: " + secretName);
            }
        } catch (SecretsManagerException e) {
            throw new RuntimeException("Error retrieving secret from AWS Secrets Manager: " + secretName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing secret: " + secretName, e);
        }
    }
}
