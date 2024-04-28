package com.travelcurator.coupon.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //JPA EntityClass들이 BaseTimeClass를 상속할 경우 BaseTime class 필드인 CreatedDate, LastModifiedDate를 인식하게 합니다.
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate //Entity가 생성되어 저장될 때 시간이 자동으로 저장
    private LocalDateTime dateCreated;

    @LastModifiedDate //조회한 Entity의 값을 변경할 때 시간이 자동으로 저장
    private LocalDateTime dateUpdated;
}
