package com.travelcurator.couponcore.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.couponcore.exception.CouponIssueException;
import com.travelcurator.couponcore.exception.ErrorCode;
import com.travelcurator.couponcore.repository.redis.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import java.util.*;

import static com.travelcurator.couponcore.exception.ErrorCode.FAIL_COUPON_ISSUE_REQUEST;
import static com.travelcurator.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.travelcurator.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Repository
public class RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<String> issueScript = issueRequestScript();
    private final String issueRequestQueueKey = getIssueRequestQueueKey();
    private final ObjectMapper objectMapper = new ObjectMapper();


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

    public Long lSize(String key){
        return redisTemplate.opsForList().size(key);
    }
    public String lIndex(String key, long index){
        return redisTemplate.opsForList().index(key, index);
    }
    public String lPop(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    public void issueRequest(long couponId, String userId, int totalIssueQuantity){
        String issueRequestKey = getIssueRequestKey(couponId);
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(couponId, userId);
        try{
            String code = redisTemplate.execute(
                    issueScript,
                    List.of(issueRequestKey, issueRequestQueueKey),
                    String.valueOf(userId),
                    String.valueOf(totalIssueQuantity),
                    objectMapper.writeValueAsString(couponIssueRequest)
            );
            CouponIssueRequestCode.checkRequestResult(CouponIssueRequestCode.find(code));
        }catch (JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(couponIssueRequest));
        }


    }
    /*
    redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
                    return '2'
    : sismember를 통해 set안에 KEYS[1], ARGV[1]에 해당하는 값이 있는지 검증하고 결과 값이 1(=이미 존재)이면 '2'를 반환한다.
    -------------------------------------------------------------------------------------------------------------
    if tonumber(ARGV[2] >  redis.call('SCARD', KEYS[1])) then
                    redis.call('SADD', KEYS[1])
                    redis.call('RPUSH', KEYS[1])
                    return '1'
    : ARGV[2] >  redis.call('SCARD', KEYS[1]) -> 쿠폰 발급 가능 수량을 비교할 때 사용한다.
      발급 수량이 남아있는 경우에 SADD 명령어와 RPUSH 명령어를 실행한다.
    -------------------------------------------------------------------------------------------------------------
    나머지의 경우엔 3을 return 한다.

     */
    public RedisScript<String> issueRequestScript(){
        String script = """
                if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
                    return '2'
                end
                                
                if tonumber(ARGV[2]) > redis.call('SCARD', KEYS[1]) then
                    redis.call('SADD', KEYS[1], ARGV[1])
                    redis.call('RPUSH', KEYS[2], ARGV[3])
                    return '1'
                end
                                
                return '3'
                """;
        return RedisScript.of(script, String.class);
    }
}
