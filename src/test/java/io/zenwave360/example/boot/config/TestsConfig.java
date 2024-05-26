package io.zenwave360.example.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.stream.schema.avro.AvroSchemaServiceManager;
import org.springframework.cloud.stream.schema.avro.AvroSchemaServiceManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestsConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestsConfig.class);

    @Bean
    public static AvroSchemaServiceManager avroSchemaServiceManager() {
        return new AvroSchemaServiceManagerImpl();
    }

    @Bean
    public static AvroSchemaMessageConverter avroSchemaMessageConverter(AvroSchemaServiceManager avroSchemaServiceManager) {
        return new AvroSchemaMessageConverter(avroSchemaServiceManager);
    }
}
