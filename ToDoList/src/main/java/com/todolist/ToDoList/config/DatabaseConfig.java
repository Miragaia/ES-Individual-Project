package com.todolist.ToDoList.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.todolist.ToDoList.service.SecretsManagerService;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.annotation.PostConstruct;  // Jakarta annotation for lifecycle management

import org.json.JSONObject;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.regions.Region;
import org.springframework.context.annotation.ComponentScan;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private final SecretsManagerService secretManagerService;

    @Autowired
    public DatabaseConfig(SecretsManagerService secretManagerService) {
        this.secretManagerService = secretManagerService;
    }

    @PostConstruct
    public void initialize() {
        // Fetch secrets from Secrets Manager
        Map<String, String> postgresSecret = secretManagerService.getSecrets("todolist-postgres-secret");
        Map<String, String> springDataSourceSecret = secretManagerService.getSecrets("todolist-spring-datasource-secret");

        // Extract the values
        String postgresUser = postgresSecret.get("POSTGRES_USER");
        String postgresPassword = postgresSecret.get("POSTGRES_PASSWORD");
        String postgresDb = postgresSecret.get("POSTGRES_DB");

        String springDataSourceUrl = springDataSourceSecret.get("SPRING_DATASOURCE_URL");
        String springDataSourceUsername = springDataSourceSecret.get("SPRING_DATASOURCE_USERNAME");
        String springDataSourcePassword = springDataSourceSecret.get("SPRING_DATASOURCE_PASSWORD");

        // Configure DataSource or other necessary configurations
        // ...
    }
}

