package com.example.ddd.order.domain.event;

import com.example.ddd.order.domain.model.Money;
import com.example.ddd.order.domain.model.OrderId;

import java.time.Instant;

/**
 * 【ドメインイベント】注文が確定した。
 * 「過去に起きた事実」なので名前は過去形、フィールドは全て final（変更不可）。
 * 在庫引当・メール通知など「確定したら起こしたい処理」を、Order に直接書かずに疎結合にするための仕組み。
 */
public final class OrderConfirmed {

    private final OrderId orderId;
    private final Money totalAmount;
    private final Instant occurredAt;

    public OrderConfirmed(OrderId orderId, Money totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.occurredAt = Instant.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
