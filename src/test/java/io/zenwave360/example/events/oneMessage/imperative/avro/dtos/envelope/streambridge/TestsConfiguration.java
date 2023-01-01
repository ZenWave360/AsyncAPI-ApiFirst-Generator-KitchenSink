package io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge;

import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerEventPayload2;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge.client.IOnCustomerEventAvroConsumerService;
import io.zenwave360.example.events.support.model.AvroEnvelopeWrapperUnWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public AvroEnvelopeWrapperUnWrapper avroEnvelopeWrapperUnWrapper() {
        return new AvroEnvelopeWrapperUnWrapper();
    }

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
            @Override
            public void onCustomerEventAvro(CustomerEventPayload2 payload, CustomerEventPayload2Headers headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

}
