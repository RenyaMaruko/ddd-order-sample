package com.example.ddd.order.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 【値オブジェクト】注文ID。
 * ただの String / UUID ではなく型にすることで、ProductId と取り違えるミスをコンパイル時に防ぐ。
 */
public final class OrderId {

    private final UUID value;

    private OrderId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(String value) {
        try {
            return new OrderId(UUID.fromString(value));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new OrderDomainException("注文IDの形式が不正です: " + value);
        }
    }

    public String value() {
        return value.toString();
    }

    // 値オブジェクトは「値が同じなら同じもの」。equals/hashCode を値で定義する。
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId)) return false;
        return value.equals(((OrderId) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
