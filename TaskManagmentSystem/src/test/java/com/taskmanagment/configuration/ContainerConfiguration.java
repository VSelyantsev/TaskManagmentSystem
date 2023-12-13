package com.taskmanagment.configuration;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class ContainerConfiguration  {
    private static final String DOCKER_IMAGE = "postgres:11.1";
    private static final String DATABASE_NAME = "testdb";
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DOCKER_IMAGE)
                .withReuse(true)
                .withDatabaseName(DATABASE_NAME);
        postgresContainer.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return postgresContainer;
    }
}