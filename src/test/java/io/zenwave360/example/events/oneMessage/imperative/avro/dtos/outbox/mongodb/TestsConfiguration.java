package io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb;

import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerRequestPayload;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.client.IOnCustomerEventAvroConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.provider.IDoCustomerRequestAvroConsumerService;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.client.CustomerCommandsProducer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.outbox.mongodb.provider.CustomerEventsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

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
        };
    }

    @Bean
    public IDoCustomerRequestAvroConsumerService doCustomerRequestAvroConsumerService() {
        return new IDoCustomerRequestAvroConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void doCustomerRequestAvro(CustomerRequestPayload payload, CustomerRequestPayloadHeaders headers) {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
                receivedHeaders.add(headers);
            }
        };
    }

    @Bean(destroyMethod = "stop")
    public MessageListenerContainer configCustomerEventOutboxCollectionChangeStreams(MongoTemplate template, CustomerEventsProducer customerEventsProducer) {
        var changeStreamOptions = ChangeStreamOptions.builder();
        final var container = new DefaultMessageListenerContainer(template);
        final var options = new ChangeStreamRequest.ChangeStreamRequestOptions(null, customerEventsProducer.onCustomerEventAvroOutboxCollection, changeStreamOptions.build());
        container.register(new ChangeStreamRequest<>(customerEventsProducer.onCustomerEventAvroMongoChangeStreamsListener, options), Map.class);
        container.start();
        return container;
    }

    @Bean(destroyMethod = "stop")
    public MessageListenerContainer configCustomerCommandOutboxCollectionChangeStreams(MongoTemplate template, CustomerCommandsProducer customerCommandsProducer) {
        var changeStreamOptions = ChangeStreamOptions.builder();
        final var container = new DefaultMessageListenerContainer(template);
        final var options = new ChangeStreamRequest.ChangeStreamRequestOptions(null, customerCommandsProducer.doCustomerRequestAvroOutboxCollection, changeStreamOptions.build());
        container.register(new ChangeStreamRequest<>(customerCommandsProducer.doCustomerRequestAvroMongoChangeStreamsListener, options), Map.class);
        container.start();
        return container;
    }
}
