package com.example.ddd.order.domain.repository;

import com.example.ddd.order.domain.model.Order;
import com.example.ddd.order.domain.model.OrderId;

import java.util.Optional;

/**
 * 【リポジトリ】集約の永続化の入口。
 * ・インターフェースは「ドメイン層」に置く（何を保存したいか＝ドメインの関心事）
 * ・実装は「インフラ層」に置く（どう保存するか＝技術の関心事）
 * これにより、ドメインはDBやJPAを知らずに済む（依存性逆転）。
 * 単位は「集約」なので、OrderLine 用のリポジトリは作らない。
 */
public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(OrderId id);
}
