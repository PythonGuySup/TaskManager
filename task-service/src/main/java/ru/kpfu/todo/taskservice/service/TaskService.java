package ru.kpfu.todo.taskservice.service;

import ru.kpfu.todo.common.dto.TaskDto;
import ru.kpfu.todo.taskservice.entity.Task;
import ru.kpfu.todo.taskservice.exception.TaskNotFoundException;
import ru.kpfu.todo.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final CacheService cacheService;
    private final EventClient eventClient;

    @Transactional
    public TaskDto createTask(String title) {
        Task task = Task.builder()
                .title(title)
                .status("NEW")
                .build();
        task = taskRepository.save(task);

        cacheService.clearTasksCache();

        eventClient.sendEvent("TASK_CREATED", task.getId(), task.getTitle(), task.getStatus());

        return new TaskDto(task.getId(), task.getTitle(), task.getStatus());
    }

    public List<TaskDto> getAllTasks() {
        var cached = cacheService.get(CacheService.TASKS_CACHE_KEY, TaskDto[].class);
        if (cached.isPresent()) {
            log.info("Returning tasks from Redis cache");
            return List.of(cached.get());
        }

        log.info("Fetching tasks from PostgreSQL");
        List<TaskDto> result = taskRepository.findAll().stream()
                .map(t -> new TaskDto(t.getId(), t.getTitle(), t.getStatus()))
                .toList();

        cacheService.set(CacheService.TASKS_CACHE_KEY, result, CacheService.TASKS_TTL);
        return result;
    }

    public TaskDto getTask(String id) {
        String key = CacheService.taskCacheKey(id);
        var cached = cacheService.get(key, TaskDto.class);
        if (cached.isPresent()) {
            log.info("Returning task {} from Redis cache", id);
            return cached.get();
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        TaskDto dto = new TaskDto(task.getId(), task.getTitle(), task.getStatus());
        cacheService.set(key, dto, CacheService.TASK_TTL);
        return dto;
    }

    @Transactional
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
        cacheService.clearTasksCache();
    }
}