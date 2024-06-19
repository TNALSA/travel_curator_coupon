# ✈️ 해외 여행 상품 추천사이트 - 쿠폰 발급

## 🎟️ 쿠폰 발급 시스템 구조도
<img src = "https://github.com/sadang-data-enginneering/TC_coupon/assets/106741517/647d569c-40d3-4b9f-91a3-fab9461956b2"/>

## 📄 데이터베이스 ERD
<img src = "https://github.com/lightening-data-masters/TC_coupon/assets/106741517/df4f85b6-6539-4d5e-ae75-a25fd6f3c239"/>

## 🥺 쿠폰 이벤트 요구사항
- 이벤트 기간 내에만 발급이 가능해야 한다.
- 유저 당 1번의 쿠폰 발급 (중복 발급 ❌)
- 선착순 쿠폰의 최대 쿠폰 발급 수량을 설정한다.
- 쿠폰 발급 요청에 대한 동시성을 제어한다.

## 📌 쿠폰 발급 기능
- 쿠폰 발급 기간 검증
- 쿠폰 발급 수량 검증
  - 쿠폰 전체 발급 수량 확인
  - 중복 발급 요청 검증
- 쿠폰 발급
  - 쿠폰 발급 수량 증가
  - 쿠폰 발급 정보 저장

## 📂 프로젝트 구성
```bash
coupon
├── coupon-api
│   ├── CouponApiApplication.java
│   ├── build.gradle
│   ├── src
├── coupon-consumer
│   ├── CouponConsumerApplication.java
│   ├── build.gradle
│   ├── src
├── coupon-core
│   ├── build.gradle
│   ├── src
├── build.gradle
```
### coupon-api
* 사용자의 요청을 Controller를 통해 전달받는 모듈
* Controller를 통해 전달받은 요청을 coupon-core의 asyncIssueRequestV2 메소드를 호출하여 Redis Queue에 사용자의 요청을 적재
  
### coupon-consumer
* Redis Queue의 적재되어 있는 데이터 주기적으로 감지하여 순차적으로 RDS에 저장
  
### coupon-core
* 실질적인 모든 기능의 중심부 역할을 하는 모듈
* Redis Queue에 요청을 적재하는 기능, RDS에 실제 데이터를 저장하는 기능을 담당
* coupon-api, coupon-consumer 두 모듈에서 공통적으로 사용하는 자원(application-core.yml(DB 정보, Redis 정보)), dependency 등을 정의
  
