package com.rate.rate_limit_api.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimiter {
    @Value("${per.user.rate.limit}")
    private Integer perUserRateLimit;

    private final ProxyManager proxyManager;

    public RateLimiter(ProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }

    public Bucket resolveBucket(Long userId) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser();
        // Does not always create a new bucket, but instead returns the existing one if it exists.
        return proxyManager.builder().build(userId, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser() {
        Refill refill = Refill.intervally(perUserRateLimit, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(perUserRateLimit, refill);
        return () -> (BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }
}
