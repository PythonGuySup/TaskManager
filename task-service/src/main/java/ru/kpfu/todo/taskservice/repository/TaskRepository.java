package ru.kpfu.todo.taskservice.repository;

import ru.kpfu.todo.taskservice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {}