package com.travelcurator.couponcore.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    RedissonClient redissonClient(){
        System.out.println("[Log]redis host:" + host);
        System.out.println("[Log]redis port:" + port);
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }
}
