package com.example.ddd.order.domain.event;

import com.example.ddd.order.domain.model.OrderId;
import com.example.ddd.order.domain.model.OrderStatus;

import java.time.Instant;

/**
 * 【ドメインイベント】注文がキャンセルされた。
 * キャンセル前の状態も持たせている。確定済み(CONFIRMED)からのキャンセルなら
 * 在庫の引当解除などが必要、作成中(DRAFT)なら不要、とリスナー側が判断できる。
 */
public final class OrderCancelled {

    private final OrderId orderId;
    private final OrderStatus previousStatus;
    private final Instant occurredAt;

    public OrderCancelled(OrderId orderId, OrderStatus previousStatus) {
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.occurredAt = Instant.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
