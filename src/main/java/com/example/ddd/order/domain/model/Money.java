package com.example.ddd.order.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 【値オブジェクト】金額（円）。
 * ・イミュータブル（足し算しても新しいインスタンスを返す）
 * ・不変条件：マイナスにならない
 * ・金額に関する振る舞い（加算・乗算・割引率計算）をここに集める
 */
public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new OrderDomainException("金額はマイナスにできません: " + amount);
        }
        this.amount = amount.setScale(0, RoundingMode.DOWN);
    }

    public static Money yen(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money multiply(int times) {
        return new Money(amount.multiply(BigDecimal.valueOf(times)));
    }

    /** 金額の percent % 分（端数切り捨て）。 */
    public Money percent(int percent) {
        return new Money(amount.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN));
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    public long longValue() {
        return amount.longValueExact();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        return amount.compareTo(((Money) o).amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString() + "円";
    }
}
