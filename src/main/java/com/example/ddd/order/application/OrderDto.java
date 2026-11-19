package com.example.ddd.order.application;

import com.example.ddd.order.domain.model.Order;
import com.example.ddd.order.domain.model.OrderLine;

import java.util.List;
import java.util.stream.Collectors;

/**
 * アプリケーション層が外へ返すデータ（DTO）。
 * ドメインオブジェクト（Order）をそのまま外に出さないことで、
 * プレゼンテーション層がドメインの内部構造や操作メソッドに触れないようにする。
 */
public class OrderDto {

    private final String id;
    private final String status;
    private final List<LineDto> lines;
    private final long subtotal;
    private final long discount;
    private final long totalAmount;

    private OrderDto(Order order) {
        this.id = order.getId().value();
        this.status = order.getStatus().name();
        this.lines = order.getLines().stream().map(LineDto::new).collect(Collectors.toList());
        this.subtotal = order.subtotal().longValue();
        this.discount = order.getDiscount().longValue();
        this.totalAmount = order.totalAmount().longValue();
    }

    public static OrderDto from(Order order) {
        return new OrderDto(order);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public List<LineDto> getLines() {
        return lines;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getDiscount() {
        return discount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public static class LineDto {
        private final String productId;
        private final String productName;
        private final long unitPrice;
        private final int quantity;
        private final long subtotal;

        private LineDto(OrderLine line) {
            this.productId = line.getProductId().value();
            this.productName = line.getProductName();
            this.unitPrice = line.getUnitPrice().longValue();
            this.quantity = line.getQuantity().value();
            this.subtotal = line.subtotal().longValue();
        }

        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public long getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public long getSubtotal() {
            return subtotal;
        }
    }
}
