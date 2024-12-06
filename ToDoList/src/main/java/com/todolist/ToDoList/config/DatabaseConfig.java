package com.todolist.ToDoList.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.todolist.ToDoList.service.SecretsManagerService;

import jakarta.annotation.PostConstruct;

import java.util.Map;

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
        Map<String, String> rdsSecrets = secretManagerService.getSecrets("todo-rds-db");

        // Extract RDS configuration
        String rdsEndpoint = rdsSecrets.get("RDS_ENDPOINT");
        String rdsUsername = rdsSecrets.get("POSTGRES_USER");
        String rdsPassword = rdsSecrets.get("POSTGRES_PASSWORD");
        String rdsDb = rdsSecrets.get("POSTGRES_DB");

        System.out.println("Retreved Database Secrets: " + rdsEndpoint + ", " + rdsUsername + ", " + rdsPassword + ", " + rdsDb);

        // Configure DataSource
        System.setProperty("spring.datasource.url", "jdbc:postgresql://" + rdsEndpoint + ":5432/" + rdsDb);
        System.setProperty("spring.datasource.username", rdsUsername);
        System.setProperty("spring.datasource.password", rdsPassword);
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(System.getProperty("spring.datasource.url"));
        dataSource.setUsername(System.getProperty("spring.datasource.username"));
        dataSource.setPassword(System.getProperty("spring.datasource.password"));
        return dataSource;
    }
}
