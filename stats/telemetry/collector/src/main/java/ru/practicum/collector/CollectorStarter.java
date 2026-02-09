package ru.practicum.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.practicum.collector.config.KafkaConfig;

@Slf4j
@SpringBootApplication
public class CollectorStarter {
    public static void main(String[] args) {
        SpringApplication.run(CollectorStarter.class, args);
    }
}
