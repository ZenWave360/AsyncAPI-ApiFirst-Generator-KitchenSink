package io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge;

import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge.provider.ICustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.getReceivedHeaders;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@DisplayName("Integration Tests: Imperative with json dtos with envelope via streambridge")
public class IntegrationTests {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ICustomerEventsProducer customerEventsProducer;
    @Autowired
    IOnCustomerEventConsumerService onCustomerEventConsumerService;

    @Test
    void onCustomerEventTest() throws InterruptedException {
        // Given
        var message = new CustomerEventPayload().withCustomerId("123").withEventType(CustomerEventPayload.EventType.CREATED);
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
