package com.travelcurator.couponcore.service;

import com.travelcurator.couponcore.repository.redis.dto.CouponRedisEntity;
import com.travelcurator.couponcore.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {
    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;
    public void issue(long couponId, long userId){
        // Redis Cache
        // CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);

        // Local Cache
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId);
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId, coupon.totalQuantity());
    }

    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity){
        if(totalIssueQuantity == null){
            redisRepository.issueRequest(couponId, userId, Integer.MAX_VALUE);
        }
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }

}
