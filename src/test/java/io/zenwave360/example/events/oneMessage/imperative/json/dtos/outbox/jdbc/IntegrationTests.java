package io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc;

import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.client.ICustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.provider.ICustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.outbox.jdbc.provider.IDoCustomerRequestConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.getReceivedHeaders;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@DisplayName("Integration Tests: Imperative with json dtos via jdbc outbox")
public class IntegrationTests {

    private Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    ICustomerCommandsProducer customerCommandsProducer;
    @Autowired
    IDoCustomerRequestConsumerService doCustomerRequestConsumerService;

    @Autowired
    ICustomerEventsProducer customerEventsProducer;
    @Autowired
    IOnCustomerEventConsumerService onCustomerEventConsumerService;

    @Test
    void doCustomerCommandTest() throws InterruptedException {
        // Given
        var message = new CustomerRequestPayload().withCustomerId("231");
        var headers = new ICustomerCommandsProducer.CustomerRequestPayloadHeaders()
                .entityId("231")
                .commonHeader("value")
                .set("undocumented", "value");
        // When
        customerCommandsProducer.doCustomerRequest(message, headers);
        // Then
        var messages = awaitReceivedMessages(doCustomerRequestConsumerService);

        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getCustomerId(), ((CustomerRequestPayload) messages.get(0)).getCustomerId());
        var receivedHeaders = getReceivedHeaders(doCustomerRequestConsumerService);
        Assertions.assertEquals("231", receivedHeaders.get(0).get("entity-id"));
        Assertions.assertEquals("value", receivedHeaders.get(0).get("undocumented"));
    }

    @Test
    void onCustomerEventTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload().withCustomerId("123");
        var headers = new ICustomerEventsProducer.CustomerEventPayloadHeaders()
                .entityId("123")
                .commonHeader("value")
                .set("undocumented", "value");
        // When
        customerEventsProducer.onCustomerEvent(message, headers);
        // Then
        var messages = awaitReceivedMessages(onCustomerEventConsumerService);

        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(message.getCustomerId(), ((CustomerEventPayload) messages.get(0)).getCustomerId());
        var receivedHeaders = getReceivedHeaders(onCustomerEventConsumerService);
        Assertions.assertEquals("123", receivedHeaders.get(0).get("entity-id"));
        Assertions.assertEquals("value", receivedHeaders.get(0).get("undocumented"));
    }

}
