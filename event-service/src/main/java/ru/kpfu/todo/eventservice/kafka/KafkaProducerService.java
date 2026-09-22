package ru.kpfu.todo.eventservice.kafka;

import ru.kpfu.todo.common.dto.EventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.tasks:tasks}")
    private String topic;

    public void sendEvent(EventRequest event) {
        String key = event.task() != null ? event.task().id() : null;
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event to Kafka", ex);
                    } else {
                        log.info("Event sent to Kafka topic={} offset={}",
                                topic, result.getRecordMetadata().offset());
                    }
                });
    }
}