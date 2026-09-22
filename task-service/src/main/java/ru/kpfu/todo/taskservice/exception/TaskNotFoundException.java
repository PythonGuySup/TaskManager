package ru.kpfu.todo.taskservice.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String id) {
        super("Task not found: " + id);
    }
}