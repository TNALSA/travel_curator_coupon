package com.travelcurator.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.couponcore.component.DistributeLockExecutor;
import com.travelcurator.couponcore.repository.redis.dto.CouponRedisEntity;
import com.travelcurator.couponcore.exception.CouponIssueException;
import com.travelcurator.couponcore.exception.ErrorCode;
import com.travelcurator.couponcore.repository.redis.RedisRepository;
import com.travelcurator.couponcore.repository.redis.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.travelcurator.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.travelcurator.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;
@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;

    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId){
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();
        distributeLockExecutor.execute("lock_%s".formatted(couponId),3000,3000, ()->{
            couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
            issueRequest(couponId, userId);
        });
    }

    /*
    * 1. totalQuantity > redisRepository.sCard(key) -> 현재 쿠폰 발급 수량에 대한 검증
    * 2. !redisRepository.sIsMember(key, String.valueOf(userId)) -> 중복 발급 검증
    * 3. redisRepository.sAdd // 쿠폰 발급 요청 저장
    * 4. redisRepository.rPush // 쿠폰 발급 큐 적재
    * */


private void issueRequest(long couponId, long userId){
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try{
            String value = objectMapper.writeValueAsString(issueRequest);
            // sAdd -> set에 새로운 요청을 삽입
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            // rPush -> queue에 데이터를 적재
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        }catch (JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"input: %s".formatted(issueRequest));
        }
}

}
