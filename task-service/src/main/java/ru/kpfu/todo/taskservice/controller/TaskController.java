package ru.kpfu.todo.taskservice.controller;

import ru.kpfu.todo.common.dto.TaskCreateRequest;
import ru.kpfu.todo.common.dto.TaskDto;
import ru.kpfu.todo.taskservice.exception.TaskNotFoundException;
import ru.kpfu.todo.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskDto createTask(@Valid @RequestBody TaskCreateRequest req) {
        return taskService.createTask(req.title());
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable String id) {
        return taskService.getTask(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return Map.of("status", "deleted");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(TaskNotFoundException e) {
        return Map.of("detail", e.getMessage());
    }
}