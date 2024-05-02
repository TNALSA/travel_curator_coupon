package com.travelcurator.coupon.exception;

public enum ErrorCode {
    INVALID_COUPON_ISSUE_QUANTITY("발급 가능한 수량을 초과합니다."),
    INVALID_COUPON_ISSUE_DATE("쿠폰 발급 기간이 유효하지 않습니다."),
    COUPON_NOT_EXIST("존재하지 않는 쿠폰입니다."),
    DUPLICATED_COUPON_ISSUE("이미 발급된 쿠폰입니다.");

    public final String messase;

    ErrorCode(String message){
        this.messase = message;
    }
}
