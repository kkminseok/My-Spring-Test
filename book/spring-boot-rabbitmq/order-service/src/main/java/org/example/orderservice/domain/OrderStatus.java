package org.example.orderservice.domain;

public enum OrderStatus {
    PENDING, // 시작전
    PROCESSING, // 재고확인 주문 진행
    COMPLETED, // 주문완료
    FAILED // 주문실패
}
