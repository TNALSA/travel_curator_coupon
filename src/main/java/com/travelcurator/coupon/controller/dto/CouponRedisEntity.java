package com.travelcurator.coupon.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import com.travelcurator.coupon.model.Coupon;
import com.travelcurator.coupon.model.CouponType;

import java.time.LocalDateTime;

/**
 * 해당 Parameter를 가져오기 위해 jackson 의존성을 추가한다.
 * @param id
 * @param c
 * @param totalQuantity
 * @param dateIssueStart
 * @param dateIssueEnd
 */
public record CouponRedisEntity(Long id,
                                CouponType c,
                                Integer totalQuantity,

                                @JsonSerialize(using = LocalDateSerializer.class)
                                @JsonDeserialize(using = LocalDateDeserializer.class)
                                LocalDateTime dateIssueStart,

                                @JsonSerialize(using = LocalDateSerializer.class)
                                @JsonDeserialize(using = LocalDateDeserializer.class)
                                LocalDateTime dateIssueEnd
) {
    public CouponRedisEntity(Coupon coupon){
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd()
        );
    }


    private boolean availableIssueDate(){
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    // 발급 기한 검증
    public void checkIssuableCoupon(){
        if(!availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다. couponId: %s, issueStart:%s, issueEnd:%s".formatted(id, dateIssueStart, dateIssueEnd));
        }

    }
}
