package ru.kpfu.todo.eventservice.service;

import ru.kpfu.todo.common.dto.EventRequest;
import ru.kpfu.todo.eventservice.document.EventDocument;
import ru.kpfu.todo.eventservice.kafka.KafkaProducerService;
import ru.kpfu.todo.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final KafkaProducerService kafkaProducer;

    public void handleEvent(EventRequest event) {
        log.info("Event received: {}", event);

        EventDocument doc = EventDocument.builder()
                .event(event)
                .receivedAt(Instant.now())
                .source("task-service")
                .build();
        eventRepository.save(doc);

        kafkaProducer.sendEvent(event);
    }
}