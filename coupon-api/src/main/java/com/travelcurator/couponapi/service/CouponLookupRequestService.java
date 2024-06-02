package com.travelcurator.couponapi.service;

import com.travelcurator.couponcore.model.CouponIssue;
import com.travelcurator.couponcore.service.CouponLookupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponLookupRequestService {
    private final CouponLookupService couponLookupService;
    List<CouponIssue> cll = new ArrayList<>();

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public List<CouponIssue> LookupRequestV1(long userId){
        cll = couponLookupService.lookupCoupon(userId);
        for(CouponIssue couponId : cll) {
            log.info("user %s 가 발급 받은 쿠폰:%s ".formatted(userId, couponId.getCouponId()));
        }

        return cll;
    }

}
