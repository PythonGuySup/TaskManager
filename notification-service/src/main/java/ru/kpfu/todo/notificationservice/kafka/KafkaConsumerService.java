package ru.kpfu.todo.notificationservice.kafka;

import ru.kpfu.todo.common.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    /**
     * Читает события из топика `tasks`, group_id = `notification-group`.
     * Аналог KafkaConsumer в Python:
     *   - topics = 'tasks'
     *   - group_id = 'notification-group'
     *   - auto_offset_reset = 'earliest'
     *   - value_deserializer = JSON
     */
    @KafkaListener(
            topics = "${kafka.topic.tasks:tasks}",
            groupId = "${spring.kafka.consumer.group-id:notification-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload EventRequest event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset) {

        // Аналог safe_deserialize: если сообщение невалидное — Spring передаст null
        if (event == null) {
            log.warn("Skipped invalid message (partition={}, offset={})", partition, offset);
            return;
        }

        log.info("Received event from Kafka [partition={}, offset={}]: {}",
                partition, offset, event);

        // Обработка события — как в Python (там просто print)
        if (event.task() == null) {
            log.warn("Event has no task payload: {}", event);
            return;
        }

        switch (event.type()) {
            case "TASK_CREATED" -> log.info(
                    "→ [Notification] New task created: id={}, title='{}', status={}",
                    event.task().id(), event.task().title(), event.task().status());

            case "TASK_UPDATED" -> log.info(
                    "→ [Notification] Task updated: id={}, status={}",
                    event.task().id(), event.task().status());

            case "TASK_DELETED" -> log.info(
                    "→ [Notification] Task deleted: id={}",
                    event.task().id());

            default -> log.warn("Unknown event type: {}", event.type());
        }
    }
}