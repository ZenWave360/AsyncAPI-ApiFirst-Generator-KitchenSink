package io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.jdbc;

import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerRequestPayload;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.jdbc.client.CustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.jdbc.client.IOnCustomerEventAvroConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.jdbc.provider.CustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.jdbc.provider.IDoCustomerRequestAvroConsumerService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
@EnableScheduling
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public IOnCustomerEventAvroConsumerService onCustomerEventAvroConsumerService() {
        return new IOnCustomerEventAvroConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void onCustomerEventAvro(CustomerEventPayload payload, CustomerEventPayloadHeaders headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

    @Bean
    public IDoCustomerRequestAvroConsumerService doCustomerRequestAvroConsumerService() {
        return new IDoCustomerRequestAvroConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void doCustomerRequestAvro(CustomerRequestPayload payload, CustomerRequestPayloadHeaders headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }


    // OutBox Configuration
    /**
     * Quick'n'dirty outbox pulling for testing purposes.
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
            String tableName = customerCommandsProducer.doCustomerRequestAvroOutboxTableName;
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
            String tableName = customerEventsProducer.onCustomerEventAvroOutboxTableName;
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
