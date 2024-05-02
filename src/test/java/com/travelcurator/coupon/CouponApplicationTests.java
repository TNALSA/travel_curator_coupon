package com.travelcurator.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.config.name = application")
@SpringBootTest
class CouponApplicationTests {

	@Test
	void contextLoads() {
	}

}
