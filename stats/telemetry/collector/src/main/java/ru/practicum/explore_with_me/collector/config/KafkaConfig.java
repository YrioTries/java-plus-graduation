package ru.practicum.explore_with_me.collector.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("collector.kafka.producer")
@RequiredArgsConstructor
public class KafkaConfig {
    private final Properties properties;
    private final String topic;
}
