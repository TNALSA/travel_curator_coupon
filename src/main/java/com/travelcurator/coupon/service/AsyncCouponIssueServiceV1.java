package com.travelcurator.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.coupon.component.DistributeLockExecutor;
import com.travelcurator.coupon.controller.dto.CouponRedisEntity;
import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import com.travelcurator.coupon.model.Coupon;
import com.travelcurator.coupon.repository.redis.RedisRepository;
import com.travelcurator.coupon.repository.redis.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.travelcurator.coupon.util.CouponRedisUtils.getIssueRequestKey;
import static com.travelcurator.coupon.util.CouponRedisUtils.getIssueRequestQueueKey;
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
private void issueRequest(long couponId, long userId){
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try{
            String value = objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        }catch (JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"input: %s".formatted(issueRequest));
        }
}

}
