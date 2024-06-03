package com.travelcurator.couponcore.repository.mysql;


import com.querydsl.jpa.JPQLQueryFactory;
import com.travelcurator.couponcore.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserLoginRepository {

    private final JPQLQueryFactory queryFactory;

//    public User findLoginUser(String id){
//        return queryFactory.selectFrom(user)
//    }
}
