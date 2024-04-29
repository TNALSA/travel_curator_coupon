package com.travelcurator.coupon.service;

import com.travelcurator.coupon.controller.dto.CouponIssueRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto){
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));

    }

}
