package ru.kpfu.todo.taskservice.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class WebController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}