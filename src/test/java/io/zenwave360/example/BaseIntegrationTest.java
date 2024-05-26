package io.zenwave360.example;

import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseIntegrationTest {

    Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected String kafkaBootstrapServers = "localhost:9092";
    protected String schemaRegistryUrl = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        clearAllSchemas();
        clearAllTopics();
    }

    protected void clearAllSchemas() {
        RestService restService = new RestService(schemaRegistryUrl);

        try {
            List<String> allSubjects = restService.getAllSubjects();
            for (String subject : allSubjects) {
                log.debug("Deleting schema for subject: {}", subject);
                restService.deleteSubject(Map.of(), subject);
            }
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    protected void clearAllTopics() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);

        try (AdminClient admin = AdminClient.create(config)) {
            Set<String> topics = admin.listTopics().names().get().stream().filter(t -> !t.startsWith("_")).collect(Collectors.toSet());
            log.debug("Deleting topics: {}", topics);
            admin.deleteTopics(topics).all().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
