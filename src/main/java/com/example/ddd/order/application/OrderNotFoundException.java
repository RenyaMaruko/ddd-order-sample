package com.example.ddd.order.application;

import com.example.ddd.order.domain.model.OrderId;

/** 指定された注文が存在しない（ユースケース実行時のエラー）。 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(OrderId id) {
        super("注文が見つかりません: " + id);
    }
}
