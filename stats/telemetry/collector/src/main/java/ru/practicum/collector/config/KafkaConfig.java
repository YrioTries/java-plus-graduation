package ru.practicum.collector.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import ru.practicum.avro.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Getter
@Setter
public class KafkaConfig {
    private Properties properties;
    private String topic;

    @PostConstruct
    public void init() {
        topic = "stats.user-actions.v1";
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
