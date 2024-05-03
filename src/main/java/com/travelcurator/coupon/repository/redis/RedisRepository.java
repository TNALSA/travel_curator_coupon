package com.travelcurator.coupon.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis의 ZADD 명령어를 호출하는 메서드
     * parameter: key(couponId), value(userId), score(timeStamp)
     * @return add()에 대한 결과 값 (true, false)
     */
    public Boolean zAdd(String key, String value, double score){
        //return redisTemplate.opsForZSet().add(key, value, score);
        return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
    }

    public Long sAdd(String key, String value){
        return redisTemplate.opsForSet().add(key,value);
    }

    public Long sCard(String key){
        return redisTemplate.opsForSet().size(key);
    }

    public Boolean sIsMember(String key, String value){
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long rPush(String key, String value){
        return redisTemplate.opsForList().rightPush(key, value);
    }
}
