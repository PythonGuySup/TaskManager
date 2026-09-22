package ru.kpfu.todo.common.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(@NotBlank String title) {}