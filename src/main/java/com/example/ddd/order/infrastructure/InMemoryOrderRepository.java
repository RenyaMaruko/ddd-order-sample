package com.example.ddd.order.infrastructure;

import com.example.ddd.order.domain.model.Order;
import com.example.ddd.order.domain.model.OrderId;
import com.example.ddd.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【インフラ層】OrderRepository の実装（メモリ上のMap）。
 * 学習用にDBなしにしている。Spring Data JPA 等に差し替えても、
 * ドメイン層・アプリケーション層は1行も変更不要、というのが依存性逆転の効果。
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }
}
