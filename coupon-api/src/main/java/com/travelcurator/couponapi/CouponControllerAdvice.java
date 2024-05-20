package com.travelcurator.couponapi;

import com.travelcurator.couponapi.controller.dto.CouponIssueResponseDto;
import com.travelcurator.couponcore.exception.CouponIssueException;
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
