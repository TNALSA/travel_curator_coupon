package com.travelcurator.coupon.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelcurator.coupon.repository.redis.RedisRepository;
import com.travelcurator.coupon.repository.redis.dto.CouponIssueRequest;
import com.travelcurator.coupon.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static com.travelcurator.coupon.util.CouponRedisUtils.getIssueRequestQueueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
@RequiredArgsConstructor
@EnableScheduling
@Component
public class CouponIssueListener {
    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String issueRequestQueueKey = getIssueRequestQueueKey();

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Scheduled(fixedDelay = 1000L)
    public void issue() throws JsonProcessingException {
        log.info("listen...");
        while(existCouponIssueTarget()){
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작 target: %s".formatted(target));
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 tartget: %s".formatted(target));
            removeIssuedTarget();
        }
    }

    private boolean existCouponIssueTarget() {
        return redisRepository.lSize(issueRequestQueueKey) > 0;
    }

    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {
        return objectMapper.readValue(redisRepository.lIndex(issueRequestQueueKey, 0), CouponIssueRequest.class);
    }

    private void removeIssuedTarget(){
        redisRepository.lPop(issueRequestQueueKey);
    }
}
