package com.ttop.spring.stock.config;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisCacheConfig {


    /* DateTimeFormat을 String 형태로 지원하기 위한 ObjectMapper */
    // @Autowired
    // private ObjectMapper objectMapper;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // @Bean
    // public ObjectMapper objectMapper() {
    // return new ObjectMapper()
    //     .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    //     .registerModule(new JavaTimeModule());
    // }

    @Bean
    public RedisCacheManager redisCacheManager() {
        //set store methodology
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .entryTtl(Duration.ofDays(1))
        .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // .serializeValuesWith(SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()));
        // .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
        
        
        //change cache remain duration for each cache name
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        // redisCacheConfigurationMap.put("CacheNames", redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)));

        //create cache manager
        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .withInitialCacheConfigurations(redisCacheConfigurationMap)
            .cacheDefaults(redisCacheConfiguration)
            .build();
    }
}