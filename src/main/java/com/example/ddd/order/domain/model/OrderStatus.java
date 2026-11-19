package com.example.ddd.order.domain.model;

/**
 * 注文の状態。
 * DRAFT(作成中) → CONFIRMED(確定) → SHIPPED(出荷済み)
 * DRAFT / CONFIRMED からは CANCELLED(キャンセル) にもなれる。
 */
public enum OrderStatus {
    DRAFT,
    CONFIRMED,
    SHIPPED,
    CANCELLED
}
