package io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb;

import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerRequestPayload;
import io.zenwave360.example.adapters.events.avro.EventType;
import io.zenwave360.example.adapters.events.avro.RequestType;
import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.client.ICustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.client.IOnCustomerEventAvroConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.provider.ICustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.provider.IDoCustomerRequestAvroConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.getReceivedHeaders;
import static io.zenwave360.example.boot.config.TestUtils.newCustomer;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@DisplayName("Integration Tests: Imperative with avro dtos via outbox.mongodb")
@ActiveProfiles("avro")
public class IntegrationTests {

    private Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    ICustomerCommandsProducer customerCommandsProducer;
    @Autowired
    IDoCustomerRequestAvroConsumerService doCustomerRequestConsumerService;

    @Autowired
    ICustomerEventsProducer customerEventsProducer;
    @Autowired
    IOnCustomerEventAvroConsumerService onCustomerEventConsumerService;

    @Test
    void doCustomerCommandTest() throws InterruptedException {
        // Given
        var message = new CustomerRequestPayload();
        message.setId("123");
        message.setPayload(newCustomer());
        message.setRequestType(RequestType.create);
        var headers = new ICustomerCommandsProducer.CustomerRequestPayloadHeaders()
                .entityId("231")
                .set("undocumented", "value");
        // When
        customerCommandsProducer.doCustomerRequestAvro(message, headers);
        // Then
        var messages = awaitReceivedMessages(doCustomerRequestConsumerService);
        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getId().toString(), ((CustomerRequestPayload) messages.get(0)).getId().toString());
        var receivedHeaders = getReceivedHeaders(doCustomerRequestConsumerService);
        Assertions.assertEquals("231", receivedHeaders.get(0).get("entity-id"));
        Assertions.assertEquals("value", receivedHeaders.get(0).get("undocumented"));
    }

    @Test
    void onCustomerEventTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload();
        message.setId("123");
        message.setPayload(newCustomer());
        message.setEventType(EventType.created);
        var headers = new ICustomerEventsProducer.CustomerEventPayloadHeaders()
                .entityId("231")
                .set("undocumented", "value");
        // When
        customerEventsProducer.onCustomerEventAvro(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventConsumerService);
        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getId().toString(), ((CustomerEventPayload) messages.get(0)).getId().toString());
        var receivedHeaders = getReceivedHeaders(onCustomerEventConsumerService);
        Assertions.assertEquals("231", receivedHeaders.get(0).get("entity-id"));
        Assertions.assertEquals("value", receivedHeaders.get(0).get("undocumented"));
    }
}
