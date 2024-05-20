package com.travelcurator.coupon.controller;

import com.travelcurator.coupon.controller.dto.CouponIssueRequestDto;
import com.travelcurator.coupon.service.CouponIssueRequestService;
import com.travelcurator.coupon.controller.dto.CouponIssueResponseDto;
import com.travelcurator.coupon.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body){
         couponIssueRequestService.issueRequestV1(body);
         return new CouponIssueResponseDto(true, null);
    }


    /**
     * redis를 이용한 쿠폰발급 요청
      * @param body
     * @return CouponIssueResponseDto -> 쿠폰 발급 성공 시 반환
     */
    @PostMapping("/v1/issue-async")
    public CouponIssueResponseDto asyncIssueV1(@RequestBody CouponIssueRequestDto body){
        couponIssueRequestService.asyncIssueRequestV1(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v2/issue-async")
    public CouponIssueResponseDto asyncIssueV2(@RequestBody CouponIssueRequestDto body){
        couponIssueRequestService.asyncIssueRequestV2(body);
        return new CouponIssueResponseDto(true, null);
    }
}
