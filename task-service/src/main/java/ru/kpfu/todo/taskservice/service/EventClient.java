package ru.kpfu.todo.taskservice.service;

import ru.kpfu.todo.common.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@Slf4j
public class EventClient {

    private final WebClient webClient;

    public EventClient(@Value("${event-service.url:http://event-service:8002}") String eventServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(eventServiceUrl).build();
    }

    public void sendEvent(String type, String id, String title, String status) {
        EventRequest payload = new EventRequest(
                type,
                new EventRequest.TaskPayload(id, title, status)
        );
        try {
            webClient.post()
                    .uri("/event")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(2))
                    .doOnSuccess(v -> log.info("Event sent: type={} taskId={}", type, id))
                    .doOnError(e -> log.error("Failed to send event: {}", e.getMessage()))
                    .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                    .block();
        } catch (Exception e) {
            log.error("Failed to send event to event-service", e);
        }
    }
}