package io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge;

import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.reactive.json.messages.streambridge.provider.IDoCustomerRequestConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

    @Bean
    public IOnCustomerEventConsumerService onCustomerEventConsumerService() {
        return new IOnCustomerEventConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void onCustomerEvent(Flux<Message<CustomerEventPayload>> messageFlux) {
                messageFlux.subscribe(message -> {
                    log.info("Received '{}' message with payload: {}", message.getClass(), message);
                    receivedMessages.add(message.getPayload());
                    receivedHeaders.add(message.getHeaders());
                });
            }
        };
    }

    @Bean
    public IDoCustomerRequestConsumerService doCustomerRequestConsumerService() {
        return new IDoCustomerRequestConsumerService() {
            public List receivedMessages = new ArrayList();
            public List receivedHeaders = new ArrayList();
            @Override
            public void doCustomerRequest(Flux<Message<CustomerRequestPayload>> messageFlux) {
                messageFlux.subscribe(message -> {
                    log.info("Received '{}' message with payload: {}", message.getClass(), message);
                    receivedMessages.add(message.getPayload());
                    receivedHeaders.add(message.getHeaders());
                });
            }
        };
    }
}
