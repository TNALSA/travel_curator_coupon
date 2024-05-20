package com.travelcurator.couponcore.repository.mysql;

import com.querydsl.jpa.JPQLQueryFactory;
import com.travelcurator.couponcore.model.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.travelcurator.couponcore.model.QCouponIssue.couponIssue;


@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private  final JPQLQueryFactory queryFactory;

    public CouponIssue findFirstCouponIssue(long couponId, long userId){
        return queryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId))
                .where(couponIssue.userId.eq(userId))
                .fetchFirst();
    }
}
