package com.travelcurator.couponcore.service;

import com.travelcurator.couponcore.exception.CouponIssueException;
import com.travelcurator.couponcore.exception.ErrorCode;
import com.travelcurator.couponcore.model.Coupon;
import com.travelcurator.couponcore.model.CouponIssue;
import com.travelcurator.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.travelcurator.couponcore.repository.mysql.CouponIssueRepository;
import com.travelcurator.couponcore.repository.mysql.CouponJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 쿠폰 발급 관련 Service Class
 * findCouponWithLock(): 쿠폰 정책에 대해 확인하는 메소드
 * issue(): 발급 기한, 수량 확인 후 쿠폰을 발급하는 메소드
 * saveCouponIssue(): 검증 완료 후 coupons_issue Table에 쿠폰 발급 내역을 저장하는 메서드
 */
@RequiredArgsConstructor
@Service
public class CouponIssueService {
    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId){
        Coupon coupon = findCouponWithLock(couponId); //해당 쿠폰이 이미 존재하는지 검증
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
    public Coupon findCouponWithLock(long couponId){
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow(() -> {
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
