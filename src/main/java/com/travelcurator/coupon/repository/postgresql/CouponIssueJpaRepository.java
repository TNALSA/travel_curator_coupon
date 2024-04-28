package com.travelcurator.coupon.repository.postgresql;

import com.travelcurator.coupon.model.Coupon;
import com.travelcurator.coupon.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
