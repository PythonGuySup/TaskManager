package ru.kpfu.todo.taskservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry registry;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        req.setAttribute("__start_ns", System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        long start = (long) req.getAttribute("__start_ns");
        long durationNs = System.nanoTime() - start;

        Counter.builder("task_service_requests_total")
                .description("Total requests")
                .tag("method", req.getMethod())
                .tag("endpoint", req.getRequestURI())
                .register(registry)
                .increment();

        Timer.builder("task_service_latency_seconds")
                .description("Request latency")
                .register(registry)
                .record(durationNs, TimeUnit.NANOSECONDS);
    }
}

@Configuration
@RequiredArgsConstructor
class WebMvcConfig implements WebMvcConfigurer {
    private final MetricsInterceptor metricsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(metricsInterceptor);
    }
}