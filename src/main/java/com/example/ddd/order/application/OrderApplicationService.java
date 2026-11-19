package com.example.ddd.order.application;

import com.example.ddd.order.domain.model.Money;
import com.example.ddd.order.domain.model.Order;
import com.example.ddd.order.domain.model.OrderId;
import com.example.ddd.order.domain.model.ProductId;
import com.example.ddd.order.domain.model.Quantity;
import com.example.ddd.order.domain.repository.OrderRepository;
import com.example.ddd.order.domain.service.DiscountPolicy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 【アプリケーションサービス】ユースケースを組み立てる層。
 *
 * 役割は「段取り」だけ：
 *   1. リポジトリから集約を取り出す
 *   2. 集約のメソッドを呼ぶ（業務ルールは集約の中にある。ここには書かない）
 *   3. リポジトリに保存する
 *   4. ドメインイベントを発行する
 *
 * ここに if 文で業務ルールを書き始めたら「ドメインモデル貧血症」のサイン。
 * 実際のDB利用時は、このクラスのメソッドに @Transactional を付けてトランザクション境界にする。
 */
@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final DiscountPolicy discountPolicy;
    private final ApplicationEventPublisher eventPublisher;

    public OrderApplicationService(OrderRepository orderRepository,
                                   DiscountPolicy discountPolicy,
                                   ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.discountPolicy = discountPolicy;
        this.eventPublisher = eventPublisher;
    }

    public OrderDto createOrder() {
        Order order = Order.create(OrderId.newId());
        return saveAndPublish(order);
    }

    public OrderDto addItem(String orderId, String productId, String productName, long unitPrice, int quantity) {
        Order order = load(orderId);
        // 入力値は値オブジェクトに変換した時点で検証される
        order.addLine(new ProductId(productId), productName, Money.yen(unitPrice), new Quantity(quantity));
        return saveAndPublish(order);
    }

    public OrderDto removeItem(String orderId, String productId) {
        Order order = load(orderId);
        order.removeLine(new ProductId(productId));
        return saveAndPublish(order);
    }

    public OrderDto confirm(String orderId) {
        Order order = load(orderId);
        order.confirm(discountPolicy);
        return saveAndPublish(order);
    }

    public OrderDto ship(String orderId) {
        Order order = load(orderId);
        order.ship();
        return saveAndPublish(order);
    }

    public OrderDto cancel(String orderId) {
        Order order = load(orderId);
        order.cancel();
        return saveAndPublish(order);
    }

    public OrderDto find(String orderId) {
        return OrderDto.from(load(orderId));
    }

    private Order load(String orderId) {
        OrderId id = OrderId.of(orderId);
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderDto saveAndPublish(Order order) {
        orderRepository.save(order);
        order.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return OrderDto.from(order);
    }
}
