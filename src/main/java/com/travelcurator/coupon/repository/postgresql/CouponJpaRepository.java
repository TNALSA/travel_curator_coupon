package com.travelcurator.coupon.repository.postgresql;

import com.travelcurator.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
