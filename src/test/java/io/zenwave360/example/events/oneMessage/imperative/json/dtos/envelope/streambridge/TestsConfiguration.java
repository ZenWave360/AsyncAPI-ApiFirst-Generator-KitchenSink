package io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge;

import io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.support.model.EnvelopeWrapperUnWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;


@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public EnvelopeWrapperUnWrapper envelopeWrapperUnWrapper() {
        // implements both wrapper and unwrapper
        return new EnvelopeWrapperUnWrapper();
    }

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
}
