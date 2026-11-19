package com.example.ddd.order.domain.model;

/**
 * 【エンティティ】注文明細。集約 Order の内部オブジェクト。
 * ・同じ注文の中では ProductId で一意に識別される
 * ・外部から直接操作させない（コンストラクタ・変更メソッドは package-private）。
 *   変更は必ず集約ルート Order を経由する。
 */
public class OrderLine {

    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private Quantity quantity;

    OrderLine(ProductId productId, String productName, Money unitPrice, Quantity quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** 同じ商品が追加されたとき、数量を合算する。 */
    void increaseQuantity(Quantity additional) {
        this.quantity = this.quantity.add(additional);
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity.value());
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
