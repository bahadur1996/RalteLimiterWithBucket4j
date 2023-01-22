package com.rate.rate_limit_api.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.Iterator;


@Configuration
public class RedisConfig  {
    @Value("${spring.data.redis.password}")
    private String redisPass;
    @Value("${spring.data.redis.port}")
    private String redisPort;
    @Value("${spring.data.redis.host}")
    private String redisHost;


    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort).setPassword(redisPass);
        return config;
    }

    @Bean
    public CacheManager cacheManager(Config config) {
        Iterator<CachingProvider> iterator = Caching.getCachingProviders(Caching.getDefaultClassLoader()).iterator();
        while(iterator.hasNext()) {
            CachingProvider provider = iterator.next();
            if (provider instanceof EhcacheCachingProvider) {
                iterator.remove();
            }
        }

        CacheManager manager = Caching.getCachingProvider().getCacheManager();
        manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        return manager;
    }

    @Bean
    ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }
}
