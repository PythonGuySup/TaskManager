package ru.kpfu.todo.eventservice.repository;

import ru.kpfu.todo.eventservice.document.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<EventDocument, String> {}