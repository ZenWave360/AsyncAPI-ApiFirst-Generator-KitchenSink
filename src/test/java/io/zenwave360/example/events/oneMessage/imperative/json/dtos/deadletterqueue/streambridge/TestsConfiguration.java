package io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge;

import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.deadletterqueue.streambridge.provider.IDoCustomerRequestConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.Message;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@ComponentScan
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
                if(payload.getCustomerId() == null) {
                    log.info("Throwing validation exception");
                    throw new ValidationException("customerId is null");
                }
                payload.getEventType().toString(); // will throw NPE if null
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
                if(payload.getCustomerId() == null) {
                    log.info("Throwing validation exception");
                    throw new ValidationException("customerId is null");
                }
                payload.getRequestType().toString(); // will throw NPE if null
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

    @Bean("do-customer-request-error")
    public Consumer<Message> doCustomerRequestErrorHandler() {
        return new Consumer<Message>() {
            public List receivedMessages = new ArrayList<>();
            public List receivedHeaders = new ArrayList();

            @Override
            public void accept(Message message) {
                log.info("Received DLQ message '{}'", message);
                receivedMessages.add(message.getPayload());
                receivedHeaders.add(message.getHeaders());
            }
        };
    }

    @Bean("on-customer-event-error")
    public Consumer<Message<String>> onCustomerEventErrorHandler() {
        return new Consumer<Message<String>>() {
            public List receivedMessages = new ArrayList<>();
            public List receivedHeaders = new ArrayList();

            @Override
            public void accept(Message<String> message) {
                log.info("Received DLQ message '{}'", message);
                receivedMessages.add(message.getPayload());
                receivedHeaders.add(message.getHeaders());
            }
        };
    }

    @Bean("on-customer-event-validation-error")
    public Consumer<Message<String>> onCustomerEventValidationErrorHandler() {
        return new Consumer<Message<String>>() {
            public List receivedMessages = new ArrayList<>();
            public List receivedHeaders = new ArrayList();

            @Override
            public void accept(Message<String> message) {
                log.info("Received DLQ message '{}'", message);
                receivedMessages.add(message.getPayload());
                receivedHeaders.add(message.getHeaders());
            }
        };
    }
}
