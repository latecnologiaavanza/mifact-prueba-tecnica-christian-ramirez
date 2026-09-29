package com.mifact.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final boolean enabled;
    private final int maxRequests;
    private final long windowMillis;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitInterceptor(
            @Value("${app.rate-limit.enabled:true}") boolean enabled,
            @Value("${app.rate-limit.max-requests:100}") int maxRequests,
            @Value("${app.rate-limit.window-seconds:60}") long windowSeconds) {
        this.enabled = enabled;
        this.maxRequests = maxRequests;
        this.windowMillis = windowSeconds * 1000L;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enabled || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String client = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        Window window = windows.compute(client, (key, current) -> {
            if (current == null || now - current.startedAt >= windowMillis) {
                return new Window(now, 1);
            }
            current.requests++;
            return current;
        });
        if (window.requests > maxRequests) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"status\":429,\"message\":\"Se excedió el límite de solicitudes. Intenta nuevamente más tarde.\"}");
            } catch (IOException ignored) {
                // La respuesta ya fue marcada con el estado 429.
            }
            return false;
        }
        return true;
    }

    private static class Window {
        private final long startedAt;
        private int requests;

        private Window(long startedAt, int requests) {
            this.startedAt = startedAt;
            this.requests = requests;
        }
    }
}
