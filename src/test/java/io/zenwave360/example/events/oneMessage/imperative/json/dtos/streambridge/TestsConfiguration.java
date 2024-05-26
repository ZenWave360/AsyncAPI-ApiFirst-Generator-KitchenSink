package io.zenwave360.example.events.oneMessage.imperative.json.dtos.streambridge;

import io.zenwave360.example.events.oneMessage.imperative.json.dtos.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.streambridge.provider.IDoCustomerRequestConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public Supplier tracingIdSupplier() {
        return () -> "test-tracing-id";
    }

    @Bean
    public IOnCustomerEventConsumerService onCustomerEventConsumerService() {
        return new OnCustomerEventConsumerService();
    }

    @Bean
    public IDoCustomerRequestConsumerService doCustomerRequestConsumerService() {
        return new DoCustomerRequestConsumerService();
    }

    private class OnCustomerEventConsumerService implements IOnCustomerEventConsumerService {
        public List receivedMessages = new ArrayList();
        public List receivedHeaders = new ArrayList();

        @Override
        public void onCustomerEvent(CustomerEventPayload payload, CustomerEventPayloadHeaders headers) {
            log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
            receivedMessages.add(payload);
            receivedHeaders.add(headers);
            log.debug("this {}", ToStringBuilder.reflectionToString(this));
        }
    }

    private class DoCustomerRequestConsumerService implements IDoCustomerRequestConsumerService {
        public List receivedMessages = new ArrayList();
        public List receivedHeaders = new ArrayList();

        @Override
        public void doCustomerRequest(CustomerRequestPayload payload, CustomerRequestPayloadHeaders headers) {
            log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
            receivedMessages.add(payload);
            receivedHeaders.add(headers);
            log.debug("this {}", ToStringBuilder.reflectionToString(this));
        }
    }
}
