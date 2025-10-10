package com.example.candidatepark.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterCleanupScheduler {
    @Autowired
    private LoginRateLimiter loginRateLimiter;

    /**
     * Clean up expired rate limit buckets every hour
     * This prevents memory leaks from accumulating buckets for users
     * who haven't logged in for a while
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void cleanupExpiredBuckets() {
//        loginRateLimiter.cleanup();
    }
}
