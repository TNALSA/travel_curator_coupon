package com.travelcurator.coupon.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis의 Sorted Set 자료구조에 데이터를 Insert하는 메서드
     * parameter: key(couponId), value(userId), score(timeStamp)
     * @return add()에 대한 결과 값 (true, false)
     */
    public Boolean zAdd(String key, String value, double score){
        //return redisTemplate.opsForZSet().add(key, value, score);
        return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
    }

    /**
     * Redis Set 자료구조에 데이터를 Insert하는 메서드
     * @param key
     * @param value
     * @return add()에 대한 결과 값 (true, false)
     */
    public Long sAdd(String key, String value){
        return redisTemplate.opsForSet().add(key,value);
    }

    /**
     * Set에 대한 카디널리티를 출력하는 메서드
     * 현재 발급된 쿠폰의 갯수를 조회할 때 사용.
     * @param key
     * @return
     */
    public Long sCard(String key){
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * Set 내부에 특정한 값이 포함되어 있는지 확인하는 메서드
     * @param key
     * @param value
     * @return isMember(key, value) 결과 값 = true of false
     */
    public Boolean sIsMember(String key, String value){
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Redis의 Queue 형태로 데이터를 저장하는 메서드
     * @param key
     * @param value
     * @return rightPush(key, value)에 대한 결과 값
     */
    public Long rPush(String key, String value){
        return redisTemplate.opsForList().rightPush(key, value);
    }
}
