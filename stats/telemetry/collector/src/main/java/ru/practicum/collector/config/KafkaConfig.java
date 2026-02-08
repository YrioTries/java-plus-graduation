package ru.practicum.collector.config;

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
}
