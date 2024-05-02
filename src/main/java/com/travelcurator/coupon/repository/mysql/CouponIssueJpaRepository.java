package com.travelcurator.coupon.repository.mysql;

import com.travelcurator.coupon.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
