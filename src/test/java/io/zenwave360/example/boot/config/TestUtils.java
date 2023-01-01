package io.zenwave360.example.boot.config;

import io.zenwave360.example.adapters.events.avro.Customer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class TestUtils {

    public static List awaitReceivedMessages(Object consumer) throws InterruptedException {
        await().atMost(5, SECONDS).until(() -> !getReceivedMessages(consumer).isEmpty());
        return getReceivedMessages(consumer);
    }

    public static List getReceivedMessages(Object consumer) {
        return (List) ReflectionTestUtils.getField(consumer, "receivedMessages");
    }
    public static List<Map> getReceivedHeaders(Object consumer) {
        return (List) ReflectionTestUtils.getField(consumer, "receivedHeaders");
    }

    public static Customer newCustomer() {
        var customer = new Customer();
        customer.setId("123");
        customer.setUsername("joe");
        customer.setPassword("123456");
        customer.setEmail("joe@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        return customer;
    }
}
