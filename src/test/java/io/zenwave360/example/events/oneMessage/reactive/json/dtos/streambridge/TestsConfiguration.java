package io.zenwave360.example.events.oneMessage.reactive.json.dtos.streambridge;

import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;
import io.zenwave360.example.events.oneMessage.model.CustomerRequestPayload;
import io.zenwave360.example.events.oneMessage.reactive.json.dtos.streambridge.client.IOnCustomerEventConsumerService;
import io.zenwave360.example.events.oneMessage.reactive.json.dtos.streambridge.provider.IDoCustomerRequestConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
public class TestsConfiguration {

    private Logger log = LoggerFactory.getLogger(TestsConfiguration.class);

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
        public void onCustomerEvent(Flux<CustomerEventPayload> msg) {
            msg.subscribe(payload -> {
                log.debug("Received message: {}", payload);
                receivedMessages.add(payload);
            });
        }
    }

    private class DoCustomerRequestConsumerService implements IDoCustomerRequestConsumerService {
        public List receivedMessages = new ArrayList();
        public List receivedHeaders = new ArrayList();

        @Override
        public void doCustomerRequest(Flux<CustomerRequestPayload> msg) {
            msg.subscribe(payload -> {
                log.info("Received '{}' message with payload: {}", payload.getClass(), payload);
                receivedMessages.add(payload);
            });
        }
    }
}
