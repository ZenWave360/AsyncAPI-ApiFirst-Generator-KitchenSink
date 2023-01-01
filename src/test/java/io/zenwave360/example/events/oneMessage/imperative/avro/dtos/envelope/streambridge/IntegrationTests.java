package io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge;

import io.zenwave360.example.adapters.events.avro.Customer;
import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerEventPayload2;
import io.zenwave360.example.adapters.events.avro.EventType;
import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge.client.IOnCustomerEventAvroConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge.provider.ICustomerEventsProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.newCustomer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@DisplayName("Integration Tests: Imperative with avro dtos via streambridge")
@ActiveProfiles("avro")
public class IntegrationTests {

    private Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    ICustomerEventsProducer customerEventsProducer;
    @Autowired
    IOnCustomerEventAvroConsumerService onCustomerEventConsumerService;

    @Test
    @Disabled // we need a proper schema-registry in memory for multiple schemas
    void onCustomerEventTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload();
        message.setId("123");
        message.setPayload(newCustomer());
        message.setEventType(EventType.created);
        var headers = new ICustomerEventsProducer.CustomerEventPayloadHeaders();
        // Given
        customerEventsProducer.onCustomerEventAvro(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventConsumerService);
        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getId().toString(), ((CustomerEventPayload) messages.get(0)).getId().toString());
    }

    @Test
    @Disabled
    void onCustomerEvent2Test() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload2();
        message.setId("123");
        message.setPayload(newCustomer());
        message.setEventType(EventType.created);
        var headers = new ICustomerEventsProducer.CustomerEventPayload2Headers();
        // Given
        customerEventsProducer.onCustomerEventAvro(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventConsumerService);
        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getId().toString(), ((CustomerEventPayload2) messages.get(0)).getId().toString());
    }

}
