package com.travelcurator.couponcore.repository.mysql;

import com.travelcurator.couponcore.model.Coupon;
import com.travelcurator.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponLookupJpaRepository extends JpaRepository<CouponIssue,Long> {
    //@Query("SELECT ci FROM CouponIssue ci INNER JOIN Coupon c ON ci.couponId = c.id WHERE ci.userId = :userId")
    @Query("SELECT ci FROM CouponIssue ci WHERE ci.userId = :userId")
    List<CouponIssue> lookupCoupon(long userId);
}
