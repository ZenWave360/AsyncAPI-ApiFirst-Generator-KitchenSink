package io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge;

import io.zenwave360.example.boot.Zenwave360ExampleApplication;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.client.ICustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.provider.ICustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.provider.IDoCustomerRequestConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static io.zenwave360.example.boot.config.TestUtils.awaitReceivedMessages;
import static io.zenwave360.example.boot.config.TestUtils.getReceivedHeaders;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@EmbeddedKafka
@SpringBootTest(classes = Zenwave360ExampleApplication.class)
@ContextConfiguration(classes = TestsConfiguration.class)
@DisplayName("Integration Tests: Reactive with json messages via streambridge")
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

        // NOTE: to receive headers, you need to set 'exposeMessages' to true in the generator configuration
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

        // NOTE: to receive headers, you need to set 'exposeMessages' to true in the generator configuration
        var receivedHeaders = getReceivedHeaders(onCustomerEventConsumerService);
        Assertions.assertEquals("123", receivedHeaders.get(0).get("entity-id"));
        Assertions.assertEquals("value", receivedHeaders.get(0).get("undocumented"));
    }

}
