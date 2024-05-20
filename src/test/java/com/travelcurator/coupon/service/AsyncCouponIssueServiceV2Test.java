package com.travelcurator.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.coupon.TestConfig;
import com.travelcurator.coupon.exception.CouponIssueException;
import com.travelcurator.coupon.exception.ErrorCode;
import com.travelcurator.coupon.model.Coupon;
import com.travelcurator.coupon.model.CouponType;
import com.travelcurator.coupon.repository.mysql.CouponJpaRepository;
import com.travelcurator.coupon.repository.redis.dto.CouponIssueRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.IntStream;

import static com.travelcurator.coupon.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static com.travelcurator.coupon.util.CouponRedisUtils.getIssueRequestKey;
import static com.travelcurator.coupon.util.CouponRedisUtils.getIssueRequestQueueKey;

class AsyncCouponIssueServiceV2Test extends TestConfig {
    @Autowired
    AsyncCouponIssueServiceV2 sut;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는 다면 예외를 반환한다.")
    void issue_1() {
        long couponId = 1;
        long userId = 1;

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issue_2() {
        // given
        long userId = 1000;
        Coupon coupon = Coupon.builder().couponType(CouponType.FIRST_COME_FIRST_SERVED).title("선착순 테스트 쿠폰").totalQuantity(10).issuedQuantity(0).dateIssueStart(LocalDateTime.now().minusDays(1)).dateIssueEnd(LocalDateTime.now().plusDays(1)).build();
        couponJpaRepository.save(coupon);
        IntStream.range(0, coupon.getTotalQuantity()).forEach(idx -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(idx));
        });
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }
    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다.")
    void issue_3() {
        long userId = 1 ;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        //when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한이 유효하지 않는 경우 예외를 반환한다.")
    void issue_4() {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        //when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다.")
    void issue_5() {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);
        //when
        sut.issue(coupon.getId(), userId);
        // then
        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(coupon.getId()), String .valueOf(userId));
        Assertions.assertTrue(isSaved);
    }
    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 요청이 설공하면 쿠폰발급 큐에 적재된다.")
    void issue_6() throws JsonProcessingException {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);
        CouponIssueRequest request = new CouponIssueRequest(coupon.getId(), userId);
        //when
        sut.issue(coupon.getId(), userId);
        // then
        String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());
        // String으로 직렬화한 결과와 비교
        Assertions.assertEquals(new ObjectMapper().writeValueAsString(request), savedIssueRequest);
    }

}