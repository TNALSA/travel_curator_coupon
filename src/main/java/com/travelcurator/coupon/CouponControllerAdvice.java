package com.travelcurator.coupon;

import com.travelcurator.coupon.controller.dto.CouponIssueResponseDto;
import com.travelcurator.coupon.exception.CouponIssueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponControllerAdvice {
    @ExceptionHandler(CouponIssueException.class)
    public CouponIssueResponseDto couponIssueExceptionHandler(CouponIssueException exception){
            return new CouponIssueResponseDto(false, exception.getErrorCode().messase);
    }

}
