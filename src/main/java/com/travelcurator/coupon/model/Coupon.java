package com.travelcurator.coupon.model;


import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.travelcurator.coupon.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //쿠폰 ID

    @Column(nullable = false)
    private String title; //쿠폰명

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType; //쿠폰 종류

    private Integer totalQuantity; //쿠폰 최대 수량

    @Column(nullable = false)
    private int issuedQuantity; //현재 쿠폰 발급 수량

    @Column(nullable = false)
    private int discountAmount; //쿠폰 할인 금액

    @Column(nullable = false)
    private int minAvailableAmount; //쿠폰 최소 사용 금액

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    //쿠폰 발급 수량 검증
    public boolean availableIssueQuantity(){
        if(totalQuantity == null){ //쿠폰 수량에 대한 정보가 없을 경우 True를 반환
            return true;
        }
        return totalQuantity > issuedQuantity;
    }

    public boolean availableIssueDate(){
        LocalDateTime now = LocalDateTime.now();
        return (dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now));
    }

    public void issue() {
        if(!availableIssueQuantity()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,"발급 가능한 수량을 초과합니다. total: %s, issued: %s".formatted(totalQuantity,issuedQuantity));
        }
        if(!availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,"발급 가능한 일자가 아닙니다. request: %s, issueStart: %s, issueEnd: %s".formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
        }
        issuedQuantity++;
    }

}
