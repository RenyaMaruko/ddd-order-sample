package com.example.ddd.order.domain.model;

import com.example.ddd.order.domain.event.OrderCancelled;
import com.example.ddd.order.domain.event.OrderConfirmed;
import com.example.ddd.order.domain.service.VolumeDiscountPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ドメインモデルのテスト。Springを起動せず、普通のJavaだけで業務ルールを検証できる。
 * これが「ドメイン層をフレームワークから独立させる」ことのご利益。
 */
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.create(OrderId.newId());
    }

    private void addPen(int quantity) {
        order.addLine(new ProductId("P-001"), "ペン", Money.yen(1_000), new Quantity(quantity));
    }

    @Test
    void 同じ商品を追加すると数量が合算される() {
        addPen(2);
        addPen(3);

        assertThat(order.getLines()).hasSize(1);
        assertThat(order.getLines().get(0).getQuantity()).isEqualTo(new Quantity(5));
    }

    @Test
    void 小計が1万円未満なら割引なし() {
        addPen(9);
        order.confirm(new VolumeDiscountPolicy());

        assertThat(order.totalAmount()).isEqualTo(Money.yen(9_000));
    }

    @Test
    void 小計が1万円以上なら10パーセント割引で確定する() {
        addPen(10);
        order.confirm(new VolumeDiscountPolicy());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getDiscount()).isEqualTo(Money.yen(1_000));
        assertThat(order.totalAmount()).isEqualTo(Money.yen(9_000));
    }

    @Test
    void 確定するとOrderConfirmedイベントが溜まる() {
        addPen(1);
        order.confirm(new VolumeDiscountPolicy());

        List<Object> events = order.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(OrderConfirmed.class);
        assertThat(order.pullDomainEvents()).isEmpty();
    }

    @Test
    void 明細が空の注文は確定できない() {
        assertThatThrownBy(() -> order.confirm(new VolumeDiscountPolicy()))
                .isInstanceOf(OrderDomainException.class);
    }

    @Test
    void 確定後は明細を追加できない() {
        addPen(1);
        order.confirm(new VolumeDiscountPolicy());

        assertThatThrownBy(() -> addPen(1)).isInstanceOf(OrderDomainException.class);
    }

    @Test
    void 出荷済みの注文はキャンセルできない() {
        addPen(1);
        order.confirm(new VolumeDiscountPolicy());
        order.ship();

        assertThatThrownBy(() -> order.cancel()).isInstanceOf(OrderDomainException.class);
    }

    @Test
    void キャンセルするとOrderCancelledイベントが溜まり_キャンセル前の状態を持つ() {
        addPen(1);
        order.confirm(new VolumeDiscountPolicy());
        order.pullDomainEvents(); // 確定イベントを捨てる

        order.cancel();

        List<Object> events = order.pullDomainEvents();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(OrderCancelled.class);
        assertThat(((OrderCancelled) events.get(0)).getPreviousStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void 数量は1未満を許さない() {
        assertThatThrownBy(() -> new Quantity(0)).isInstanceOf(OrderDomainException.class);
    }

    @Test
    void 金額はマイナスを許さない() {
        assertThatThrownBy(() -> Money.yen(-1)).isInstanceOf(OrderDomainException.class);
    }
}
