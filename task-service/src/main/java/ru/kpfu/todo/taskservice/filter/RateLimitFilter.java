package ru.kpfu.todo.taskservice.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;

    private static final int READ_LIMIT = 10;
    private static final int WRITE_LIMIT = 5;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        // Не лимитируем actuator и статику
        if (path.startsWith("/actuator") || path.startsWith("/static")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        boolean isWrite = !"GET".equalsIgnoreCase(request.getMethod());
        int limit = isWrite ? WRITE_LIMIT : READ_LIMIT;

        String bucketKey = "rl:" + ip + ":" + (isWrite ? "w" : "r");
        Bucket bucket = proxyManager.builder().build(bucketKey,
                () -> Bucket.builder()
                        .addLimit(Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofSeconds(1))))
                        .build().getConfiguration());

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"detail\":\"Rate limit exceeded\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}