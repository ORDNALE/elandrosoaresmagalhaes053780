package com.elandroapi.core.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Priority(Priorities.AUTHENTICATION + 1)
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(RateLimitFilter.class);

    private static final int MAX_REQUESTS = 10;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(1);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientId = resolveClientId(requestContext);
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createBucket);

        if (!bucket.tryConsume(1)) {
            LOG.warnf("Rate limit excedido para cliente: %s", clientId);
            requestContext.abortWith(
                    Response.status(Response.Status.TOO_MANY_REQUESTS)
                            .header("X-Rate-Limit-Limit", MAX_REQUESTS)
                            .header("X-Rate-Limit-Remaining", 0)
                            .header("X-Rate-Limit-Reset", TIME_WINDOW.toSeconds())
                            .entity("{\"error\": \"Rate limit excedido. Tente novamente em 1 minuto.\"}")
                            .build()
            );
        }
    }

    private String resolveClientId(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return "token:" + authHeader.substring(7, Math.min(authHeader.length(), 50));
        }

        String forwarded = requestContext.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return "ip:" + forwarded.split(",")[0].trim();
        }

        return "ip:" + requestContext.getHeaderString("X-Real-IP");
    }

    private Bucket createBucket(String clientId) {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(MAX_REQUESTS, TIME_WINDOW))
                .build();
    }
}
