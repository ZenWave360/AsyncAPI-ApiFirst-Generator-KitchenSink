package io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge;

import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.client.ICustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.provider.ICustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.provider.IDoCustomerRequestConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.getReceivedHeaders;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@ActiveProfiles("deadletterqueue")
@DisplayName("Integration Tests: Imperative with json dtos with deadletterqueue via streambridge")
public class IntegrationTests {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ICustomerCommandsProducer customerCommandsProducer;
    @Autowired
    IDoCustomerRequestConsumerService doCustomerRequestConsumerService;

    @Autowired
    ICustomerEventsProducer customerEventsProducer;
    @Autowired
    IOnCustomerEventConsumerService onCustomerEventConsumerService;
    @Autowired @Qualifier("on-customer-event-error")
    Object onCustomerEventErrorHandler;
    @Autowired @Qualifier("on-customer-event-validation-error")
    Object onCustomerEventValidationErrorHandler;

    @Test
    void onCustomerEventDefaultDeadLetterQueueTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload()
                .withCustomerId("123")
                .withEventType(null); // will throw NullPointerException
        var headers = new ICustomerEventsProducer.CustomerEventPayloadHeaders();
        // When
        customerEventsProducer.onCustomerEvent(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventErrorHandler);
        Assertions.assertEquals(1, messages.size());
//        Assertions.assertEquals(message.getCustomerId(), ((CustomerEventPayload) messages.get(0)).getCustomerId());

        var receivedHeaders = getReceivedHeaders(onCustomerEventErrorHandler);
        Assertions.assertEquals(NullPointerException.class.getName(), receivedHeaders.get(0).get("x-exception-type"));
    }

    @Test
    void onCustomerEventCustomDeadLetterQueueTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload()
                .withCustomerId(null) // will throw validation error
                .withEventType(CustomerEventPayload.EventType.UPDATED);
        var headers = new ICustomerEventsProducer.CustomerEventPayloadHeaders();
        // When
        customerEventsProducer.onCustomerEvent(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventValidationErrorHandler);
        Assertions.assertEquals(1, messages.size());
//        Assertions.assertEquals(message.getCustomerId(), ((CustomerEventPayload) messages.get(0)).getCustomerId());

        var receivedHeaders = getReceivedHeaders(onCustomerEventValidationErrorHandler);
        Assertions.assertEquals("customerId is null", receivedHeaders.get(0).get("x-exception-message"));
    }

}
