package ru.practicum.collector.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    private Properties properties = new Properties();
    private String topic;

    @PostConstruct
    public void debug() {
        System.out.println("=== KAFKA CONFIG DEBUG ===");
        System.out.println("properties: " + properties);
        System.out.println("keys: " + properties.stringPropertyNames());
        System.out.println("key.serializer: " + properties.getProperty("key.serializer"));
        System.out.println("========================");
    }

}
