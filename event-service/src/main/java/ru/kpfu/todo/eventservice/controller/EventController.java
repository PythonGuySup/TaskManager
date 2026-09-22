package ru.kpfu.todo.eventservice.controller;

import ru.kpfu.todo.common.dto.EventRequest;
import ru.kpfu.todo.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Map<String, String> handleEvent(@RequestBody EventRequest event) {
        eventService.handleEvent(event);
        return Map.of("status", "sent to kafka");
    }
}