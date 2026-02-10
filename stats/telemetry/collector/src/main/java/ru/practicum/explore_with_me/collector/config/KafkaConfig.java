package ru.practicum.explore_with_me.collector.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Component;
import ru.practicum.avro.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Getter
@Component
public class KafkaConfig {
    private Properties properties;
    private final String topic = "stats.user-actions.v1";

    @PostConstruct
    public void init() {
        this.properties = new Properties();
        this.properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        this.properties.put(ProducerConfig.ACKS_CONFIG, "1");
        this.properties.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        this.properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        this.properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    }
}