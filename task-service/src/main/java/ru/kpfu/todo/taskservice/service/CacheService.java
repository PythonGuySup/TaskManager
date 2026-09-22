package ru.kpfu.todo.taskservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public static final String TASKS_CACHE_KEY = "tasks:all";
    public static final Duration TASKS_TTL = Duration.ofSeconds(30);
    public static final Duration TASK_TTL = Duration.ofSeconds(60);

    public static String taskCacheKey(String id) {
        return "task:" + id;
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        String cached = redis.opsForValue().get(key);
        if (cached == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(cached, clazz));
        } catch (Exception e) {
            log.warn("Failed to deserialize cache for key={}", key, e);
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(value), ttl);
            log.debug("Cached key={} ttl={}s", key, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Cache set failed for key={}", key, e);
        }
    }

    public void delete(String key) {
        redis.delete(key);
    }

    public void clearTasksCache() {
        redis.delete(TASKS_CACHE_KEY);
        Set<String> keys = redis.keys("task:*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }
}