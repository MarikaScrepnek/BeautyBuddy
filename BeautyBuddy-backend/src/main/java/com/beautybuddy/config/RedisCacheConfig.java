package com.beautybuddy.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@Profile("!test")
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Embeds type info so Jackson knows what to deserialize back to
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration feedDefaults = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofMinutes(2))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            );

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(feedDefaults)
            .withCacheConfiguration(PRODUCT_DETAILS_CACHE, feedDefaults.entryTtl(Duration.ofMinutes(20)))
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
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache GET failed for cache={} key={}. Falling back to source.", safeCacheName(cache), key, e);
                // intentionally not rethrowing — falls back to the real method
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Cache PUT failed for cache={} key={}. Skipping cache write.", safeCacheName(cache), key, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache EVICT failed for cache={} key={}. Continuing without eviction.", safeCacheName(cache), key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Cache CLEAR failed for cache={}. Continuing without clear.", safeCacheName(cache), e);
            }

            private String safeCacheName(Cache cache) {
                return cache == null ? "unknown" : cache.getName();
            }
        };
    }
}