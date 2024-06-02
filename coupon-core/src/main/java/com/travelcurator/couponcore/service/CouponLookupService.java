package com.travelcurator.couponcore.service;

import com.travelcurator.couponcore.exception.CouponIssueException;
import com.travelcurator.couponcore.exception.ErrorCode;
import com.travelcurator.couponcore.model.CouponIssue;
import com.travelcurator.couponcore.repository.mysql.CouponLookupJpaRepository;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.travelcurator.couponcore.exception.ErrorCode.NOT_ISSUED_COUPON;

@RequiredArgsConstructor
@Service
public class CouponLookupService {
    private final CouponLookupJpaRepository couponLookupJpaRepository;
    @Transactional
    public List<CouponIssue> lookupCoupon(long userId){
        if(couponLookupJpaRepository.lookupCoupon(userId).isEmpty()){
            throw new CouponIssueException(NOT_ISSUED_COUPON,"발급된 쿠폰이 존재하지 않습니다.");
        }
        return couponLookupJpaRepository.lookupCoupon(userId);
    }
}
