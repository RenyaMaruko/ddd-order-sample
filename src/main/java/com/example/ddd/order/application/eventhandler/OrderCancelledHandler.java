package com.example.ddd.order.application.eventhandler;

import com.example.ddd.order.domain.event.OrderCancelled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 【アプリケーション層】注文キャンセルイベントのハンドラ。
 * OrderConfirmedHandler とは別クラス・別イベント型なので、互いに影響しない。
 *
 * 「確定済みからのキャンセルなら在庫の引当を解除する」といった業務ルールは、
 * ここに if で書かず、在庫の集約（例：Stock.release()）に持たせる。
 * このハンドラは、在庫を取得して release() を呼び、保存するだけの段取りになる。
 */
@Component
public class OrderCancelledHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelledHandler.class);

    @EventListener
    public void on(OrderCancelled event) {
        // 今はログのみ。在庫の集約を追加したら、ここから呼び出す。
        log.info("注文キャンセルイベント受信: orderId={}, previousStatus={}, at={}",
                event.getOrderId(), event.getPreviousStatus(), event.getOccurredAt());
    }
}
