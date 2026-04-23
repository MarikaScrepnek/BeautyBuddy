package com.beautybuddy.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    public static final String PRODUCT_DETAILS_CACHE = "product-details-v2";
    public static final String DISCUSSION_FEED_CACHE = "discussion-feed-v2";
    public static final String DISCUSSION_SEARCH_FEED_CACHE = "discussion-search-feed-v2";
    public static final String REVIEW_FEED_CACHE = "review-feed-v2";
    public static final String REVIEW_SEARCH_FEED_CACHE = "review-search-feed-v2";
    public static final String QA_FEED_CACHE = "qa-feed-v2";
    public static final String QA_SEARCH_FEED_CACHE = "qa-search-feed-v2";
    public static final String WISHLIST_CACHE = "wishlist-v1";
    public static final String ROUTINE_CACHE = "routine-v1";
    public static final String BREAKOUT_LIST_CACHE = "breakout-list-v1";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration feedDefaults = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofMinutes(2))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(feedDefaults)
            .withCacheConfiguration(
                PRODUCT_DETAILS_CACHE,
                feedDefaults.entryTtl(Duration.ofMinutes(20))
            )
            .withCacheConfiguration(DISCUSSION_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(DISCUSSION_SEARCH_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(REVIEW_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(REVIEW_SEARCH_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(QA_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(QA_SEARCH_FEED_CACHE, feedDefaults)
            .withCacheConfiguration(WISHLIST_CACHE, feedDefaults.entryTtl(Duration.ofMinutes(10)))
            .withCacheConfiguration(ROUTINE_CACHE, feedDefaults.entryTtl(Duration.ofMinutes(10)))
            .withCacheConfiguration(BREAKOUT_LIST_CACHE, feedDefaults.entryTtl(Duration.ofMinutes(10)))
            .build();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache GET failed for cache={} key={}. Falling back to source.", safeCacheName(cache), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Cache PUT failed for cache={} key={}. Skipping cache write.", safeCacheName(cache), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache EVICT failed for cache={} key={}. Continuing without eviction.", safeCacheName(cache), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Cache CLEAR failed for cache={}. Continuing without clear.", safeCacheName(cache), exception);
            }

            private String safeCacheName(Cache cache) {
                return cache == null ? "unknown" : cache.getName();
            }
        };
    }
}
