package com.example.ddd.order.domain.model;

import com.example.ddd.order.domain.event.OrderCancelled;
import com.example.ddd.order.domain.event.OrderConfirmed;
import com.example.ddd.order.domain.service.DiscountPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 【集約ルート】注文。
 *
 * 集約とは「一貫性を保つ単位」。Order と OrderLine は常にセットで扱い、
 * 外部からは必ず Order を入口にして操作する。
 * 業務ルール（不変条件）は setter ではなく、この中の「意味のあるメソッド」で守る。
 *
 * 守っているルール:
 *  - 明細の追加・削除は DRAFT のときだけ
 *  - 明細は最大 MAX_LINES 件
 *  - 確定には明細が1件以上必要
 *  - 出荷は CONFIRMED のときだけ
 *  - 出荷済み / キャンセル済みはキャンセルできない
 */
public class Order {

    private static final int MAX_LINES = 10;

    private final OrderId id;
    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status;
    private Money discount = Money.ZERO;

    // 集約が発生させたドメインイベントを溜めておく（発行はアプリケーション層の仕事）
    private final List<Object> domainEvents = new ArrayList<>();

    private Order(OrderId id) {
        this.id = id;
        this.status = OrderStatus.DRAFT;
    }

    /** ファクトリメソッド：生成時に正しい初期状態（DRAFT・明細なし）を保証する。 */
    public static Order create(OrderId id) {
        return new Order(id);
    }

    // ---- 振る舞い（ユースケースの単位ではなく「業務上の操作」の単位） ----

    public void addLine(ProductId productId, String productName, Money unitPrice, Quantity quantity) {
        requireStatus(OrderStatus.DRAFT, "明細を追加できるのは作成中の注文だけです");

        Optional<OrderLine> existing = findLine(productId);
        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
            return;
        }
        if (lines.size() >= MAX_LINES) {
            throw new OrderDomainException("明細は最大" + MAX_LINES + "件までです");
        }
        lines.add(new OrderLine(productId, productName, unitPrice, quantity));
    }

    public void removeLine(ProductId productId) {
        requireStatus(OrderStatus.DRAFT, "明細を削除できるのは作成中の注文だけです");
        boolean removed = lines.removeIf(line -> line.getProductId().equals(productId));
        if (!removed) {
            throw new OrderDomainException("該当する明細がありません: " + productId);
        }
    }

    public void confirm(DiscountPolicy discountPolicy) {
        requireStatus(OrderStatus.DRAFT, "確定できるのは作成中の注文だけです");
        if (lines.isEmpty()) {
            throw new OrderDomainException("明細が空の注文は確定できません");
        }
        this.discount = discountPolicy.discountFor(subtotal());
        this.status = OrderStatus.CONFIRMED;
        domainEvents.add(new OrderConfirmed(id, totalAmount()));
    }

    public void ship() {
        requireStatus(OrderStatus.CONFIRMED, "出荷できるのは確定済みの注文だけです");
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            throw new OrderDomainException("出荷済み・キャンセル済みの注文はキャンセルできません");
        }
        OrderStatus previous = this.status;
        this.status = OrderStatus.CANCELLED;
        domainEvents.add(new OrderCancelled(id, previous));
    }

    // ---- 計算（状態から導出できる値は保存せず、メソッドで計算する） ----

    public Money subtotal() {
        Money sum = Money.ZERO;
        for (OrderLine line : lines) {
            sum = sum.add(line.subtotal());
        }
        return sum;
    }

    public Money totalAmount() {
        return subtotal().subtract(discount);
    }

    // ---- ドメインイベント ----

    /** 溜まっているイベントを取り出して空にする。 */
    public List<Object> pullDomainEvents() {
        List<Object> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // ---- 参照用 getter（setter は意図的に作らない） ----

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getDiscount() {
        return discount;
    }

    /** 内部リストをそのまま返すと外から壊されるので、読み取り専用で返す。 */
    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    // ---- private ----

    private Optional<OrderLine> findLine(ProductId productId) {
        return lines.stream().filter(l -> l.getProductId().equals(productId)).findFirst();
    }

    private void requireStatus(OrderStatus expected, String message) {
        if (status != expected) {
            throw new OrderDomainException(message + "（現在の状態: " + status + "）");
        }
    }
}
