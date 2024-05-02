package com.travelcurator.coupon.service;

import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import com.travelcurator.coupon.model.Coupon;
import com.travelcurator.coupon.model.CouponIssue;
import com.travelcurator.coupon.repository.mysql.CouponIssueJpaRepository;
import com.travelcurator.coupon.repository.mysql.CouponIssueRepository;
import com.travelcurator.coupon.repository.mysql.CouponJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueService {
    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId){
        Coupon coupon = findCoupon(couponId); //해당 쿠폰이 이미 존재하는지 검증
        coupon.issue(); //수량, 기한 검증, 발급 수량 증가
        saveCouponIssue(couponId, userId); //coupon insert
    }

    @Transactional
    public Coupon findCoupon(long couponId){
        return couponJpaRepository.findById(couponId).orElseThrow(() -> {
            throw new CouponIssueException(ErrorCode.COUPON_NOT_EXIST,"쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId));
        });
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId){
        checkAlreadyIssuance(couponId, userId);
        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(issue);
    }

    private void checkAlreadyIssuance(long couponId, long userId){
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        if(issue != null){
            throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE,"이미 발급된 쿠폰입니다. user_id: %s, coupon_id: %s".formatted(userId,couponId));
        }
    }
}
