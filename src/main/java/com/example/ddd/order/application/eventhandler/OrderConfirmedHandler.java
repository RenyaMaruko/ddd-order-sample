package com.example.ddd.order.application.eventhandler;

import com.example.ddd.order.domain.event.OrderConfirmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 【アプリケーション層】注文確定イベントのハンドラ。
 * 「注文が確定したら何をするか」という段取り（ユースケース）を担当する。
 * 業務ルールはここに書かず、集約やドメインサービスに任せる。
 * メール送信などの外部連携は、インターフェース経由で呼び、実装は infrastructure に置く。
 * Order 側はこのクラスの存在を知らない（イベントを発行するだけ）。
 */
@Component
public class OrderConfirmedHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmedHandler.class);

    @EventListener
    public void on(OrderConfirmed event) {
        // 今はログのみ。実務ならここで在庫引当やメール送信を依頼する。
        log.info("注文確定イベント受信: orderId={}, total={}, at={}",
                event.getOrderId(), event.getTotalAmount(), event.getOccurredAt());
    }
}
