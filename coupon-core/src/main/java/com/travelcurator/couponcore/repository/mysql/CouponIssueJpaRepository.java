package com.travelcurator.couponcore.repository.mysql;

import com.travelcurator.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
