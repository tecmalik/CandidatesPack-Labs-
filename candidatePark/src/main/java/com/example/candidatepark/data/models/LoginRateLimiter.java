package com.example.candidatepark.data.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SIZE_SECONDS = 300; // 5 minutes

    public boolean allowRequest(String identifier) {
        RateLimitBucket bucket = buckets.computeIfAbsent(
                identifier,
                k -> new RateLimitBucket(MAX_ATTEMPTS, WINDOW_SIZE_SECONDS)
        );

        return bucket.tryConsume();
//    private LoginRateLimiter loginRateLimiter;
    }

    public void resetLimit(String identifier) {
        buckets.remove(identifier);
    }

    public long getRemainingAttempts(String identifier) {
        RateLimitBucket bucket = buckets.get(identifier);
        return bucket != null ? bucket.getAvailableTokens() : MAX_ATTEMPTS;
    }

    private static class RateLimitBucket {
        private long tokens;
        private final long maxTokens;
        private final long windowSizeSeconds;
        private Instant lastRefillTime;

        public RateLimitBucket(long maxTokens, long windowSizeSeconds) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.windowSizeSeconds = windowSizeSeconds;
            this.lastRefillTime = Instant.now();
        }

        public synchronized boolean tryConsume() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        public synchronized long getAvailableTokens() {
            refill();
            return tokens;
        }

        private void refill() {
            Instant now = Instant.now();
            long secondsPassed = now.getEpochSecond() - lastRefillTime.getEpochSecond();

            if (secondsPassed >= windowSizeSeconds) {
                tokens = maxTokens;
                lastRefillTime = now;
            }
        }
    }

}
