package com.example.ddd.order.domain.model;

/**
 * 【値オブジェクト】数量。
 * 「1以上、100以下」というルールを型の中に閉じ込める。
 * Quantity を受け取るコードは、値が正しいことを再チェックしなくてよい。
 */
public final class Quantity {

    private static final int MAX = 100;

    private final int value;

    public Quantity(int value) {
        if (value < 1 || value > MAX) {
            throw new OrderDomainException("数量は1〜" + MAX + "の範囲で指定してください: " + value);
        }
        this.value = value;
    }

    public Quantity add(Quantity other) {
        return new Quantity(value + other.value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity)) return false;
        return value == ((Quantity) o).value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
