package com.travelcurator.couponcore.util;

public class CouponRedisUtils {

    public static String getIssueRequestKey(long couponId){
        return "issue.request.couponId=%s".formatted(couponId);
    }

    public static String getIssueRequestQueueKey(){
        return "issue.request";
    }
}
