package ru.kpfu.todo.eventservice.document;

import ru.kpfu.todo.common.dto.EventRequest;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDocument {

    @Id
    private String id;

    private EventRequest event;
    private Instant receivedAt;
    private String source;
}