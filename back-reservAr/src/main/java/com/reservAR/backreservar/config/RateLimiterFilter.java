package com.reservAR.backreservar.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucketForLogin(){
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        if ("auth/user/login".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            Bucket bucket = buckets.computeIfAbsent("login:" + ip, key -> newBucketForLogin());

            if (!bucket.tryConsume(1)){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {"Title": "Too many requests", "Details": "Rate limit exceeded"}
                        """);
                return;
            }
        }

        if ("/reservations".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            Bucket bucket = buckets.computeIfAbsent("reservation:" + ip, key -> newBucketForLogin());

            if (!bucket.tryConsume(1)){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {"Title": "Too many requests", "Details": "Rate limit exceeded"}
                        """);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()){
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
