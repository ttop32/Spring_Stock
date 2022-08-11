package com.ttop.spring.stock.config;




import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;


// https://github.com/kstyrc/embedded-redis/issues/51
    
// profile이 아래일떄만 적용
@Profile({"embeddedRedis"})
@Configuration
@Slf4j
public class EmbeddedRedisConfig {
    
    
    @Value("${spring.redis.port}")
    private int redisPort;
    private static RedisServer redisServer = null;


    @PostConstruct
    public void startRedis() {
        if (redisServer == null || !redisServer.isActive()) {
            try {
                redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 128M")
                .build();
                redisServer.start();
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}

