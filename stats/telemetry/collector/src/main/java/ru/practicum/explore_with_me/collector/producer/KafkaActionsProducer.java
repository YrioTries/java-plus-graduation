package ru.practicum.explore_with_me.collector.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaActionsProducer {

    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;

    @Value("${collector.kafka.producer.topic}")
    private String userActionTopic;

    public CompletableFuture<SendResult<String, UserActionAvro>> send(UserActionAvro userAction) {
        String key = generateKey(userAction); // Пример: userAction.getUserId() + "_" + userAction.getTimestamp()

        CompletableFuture<SendResult<String, UserActionAvro>> future =
                kafkaTemplate.send(userActionTopic, key, userAction);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Message sent successfully to topic {}: partition={}, offset={}",
                        userActionTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic {}", userActionTopic, ex);
                // Здесь можно добавить логику повторной отправки или сохранения в Dead Letter Queue
            }
        });

        return future;
    }

    private String generateKey(UserActionAvro userAction) {
        // Генерация ключа для обеспечения порядка сообщений
        return userAction.getUserId() != null ?
                userAction.getUserId().toString() :
                "anonymous";
    }

    public void flush() {
        kafkaTemplate.flush();
    }
}