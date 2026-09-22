package ru.kpfu.todo.common.dto;

public record EventRequest(String type, TaskPayload task) {
    public record TaskPayload(String id, String title, String status) {}
}