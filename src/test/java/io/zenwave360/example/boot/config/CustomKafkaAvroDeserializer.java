package io.zenwave360.example.boot.config;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.zenwave360.example.adapters.events.avro.CustomerEventPayload;
import io.zenwave360.example.adapters.events.avro.CustomerRequestPayload;
import org.apache.avro.Schema;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {
    @Override
    public Object deserialize(String topic, byte[] bytes) {
        if (topic.equals("customer.requests.avro")) {
            this.schemaRegistry = getMockClient(CustomerRequestPayload.SCHEMA$);
        }
        if (topic.equals("customer.events.avro")) {
            // TODO configure multiple avro schemas for CustomerEventPayload and CustomerEventPayload2
            this.schemaRegistry = getMockClient(CustomerEventPayload.SCHEMA$);
        }
        return super.deserialize(topic, bytes);
    }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }
}
