package com.travelcurator.coupon;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.config.name=application")
@SpringBootTest(classes = CouponApplication.class )
@Transactional
public class TestConfig {

}
