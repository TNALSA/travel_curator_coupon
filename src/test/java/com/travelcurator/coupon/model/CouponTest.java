package com.travelcurator.coupon.model;

import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아있다면 true를 반환한다.")
    void availableIssueQuantity_1(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        boolean result = coupon.availableIssueQuantity();

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한이 시작되지 않았다면 false를 반환한다.")
    void availableIssueDate_1(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        boolean result = coupon.availableIssueDate();

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 기한에 해당되면 true를 반환한다.")
    void availableIssueDate_2(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        boolean result = coupon.availableIssueDate();

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한 종료되면 false를 반환한다.")
    void availableIssueDate_3(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        boolean result = coupon.availableIssueDate();

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 수량과 발급 기한이 유효할 때 발급에 성공")
    void issue_1(){
        //given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        coupon.issue();

        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("발급 수량을 초과하면 예외를 반환한다.")
    void issue_2(){
        //given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();


        //when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.pbulicINVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("발급 기간이 아니면 예외를 반환")
    void issue_3(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        //when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

}