package com.example.ddd.order.domain.model;

/**
 * 【値オブジェクト】商品ID。
 * 注文コンテキストでは商品そのものではなく「商品を参照するID」だけを持つ（他集約へはIDで参照する）。
 */
public final class ProductId {

    private final String value;

    public ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new OrderDomainException("商品IDは必須です");
        }
        this.value = value.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId)) return false;
        return value.equals(((ProductId) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
