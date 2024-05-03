package com.travelcurator.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.coupon.component.DistributeLockExecutor;
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
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId){
        Coupon coupon = couponIssueService.findCoupon(couponId);
        if(!coupon.availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다. couponId: %s, userId: %s".formatted(couponId, userId));
        }
        distributeLockExecutor.execute("lock_%s".formatted(couponId),3000,3000, ()->{
            if(!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)){
                throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,"발급 가능한 수량을 초과합니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }
            if(!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)){
                throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE,"이미 발급 요청이 처리되었습니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }
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


        //쿠폰 발급 큐 적재

}

}
