package io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.client.CustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.provider.CustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.provider.IDoCustomerRequestConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
@EnableScheduling
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public IOnCustomerEventConsumerService onCustomerEventConsumerService() {
        return new IOnCustomerEventConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void onCustomerEvent(CustomerEventPayload payload, CustomerEventPayloadHeaders headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

    @Bean
    public IDoCustomerRequestConsumerService doCustomerRequestConsumerService() {
        return new IDoCustomerRequestConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void doCustomerRequest(CustomerRequestPayload payload, CustomerRequestPayloadHeaders headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

    // OutBox Configuration
    /**
     * Do not use in production, just for quick outbox pulling testing
     */
    @Component
    class OutboxPullingScheduler {
        @Autowired
        JdbcTemplate jdbcTemplate;
        @Autowired
        CustomerCommandsProducer customerCommandsProducer;
        @Autowired
        CustomerEventsProducer customerEventsProducer;
        @Scheduled(fixedDelay = 1000)
        public void pullCustomerCommandsProducerOutbox() {
            String tableName = customerCommandsProducer.doCustomerRequestOutboxTableName;
            log.info("Pulling outbox table: {}", tableName);
            var rows = jdbcTemplate.queryForList("SELECT * FROM " + tableName + " WHERE sent_at IS NULL ORDER BY id ASC");
            log.info("Found {} rows", rows.size());
            for (var row : rows) {
                try {
                    processCustomerCommandsProducerOutboxRow(row, tableName);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void processCustomerCommandsProducerOutboxRow(Map<String, Object> row, String tableName) throws Exception {
            customerCommandsProducer.sendOutboxMessage(row);
            jdbcTemplate.update("UPDATE " + tableName + " SET sent_at = current_timestamp() WHERE id = ?", row.get("id"));
        }

        @Scheduled(fixedDelay = 1000)
        public void pullCustomerEventsProducerOutbox() {
            String tableName = customerEventsProducer.onCustomerEventOutboxTableName;
            log.info("Pulling outbox table: {}", tableName);
            var rows = jdbcTemplate.queryForList("SELECT * FROM " + tableName + " WHERE sent_at IS NULL ORDER BY id ASC");
            log.info("Found {} rows", rows.size());
            for (var row : rows) {
                try {
                    processCustomerEventsProducerOutboxRow(row, tableName);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void processCustomerEventsProducerOutboxRow(Map<String, Object> row, String tableName) throws Exception {
            customerEventsProducer.sendOutboxMessage(row);
            jdbcTemplate.update("UPDATE " + tableName + " SET sent_at = current_timestamp() WHERE id = ?", row.get("id"));
        }
    }

}
