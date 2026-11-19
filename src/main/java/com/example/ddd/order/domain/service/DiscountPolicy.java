package com.example.ddd.order.domain.service;

import com.example.ddd.order.domain.model.Money;

/**
 * 【ドメインサービス】割引ポリシー。
 * 「割引額をどう決めるか」は特定のエンティティの責務にしづらい業務知識なので、
 * 独立したドメインのインターフェースとして表現する（Strategyパターン）。
 * Order はこのインターフェースだけを知り、具体的な割引ルールは知らない。
 */
public interface DiscountPolicy {

    Money discountFor(Money subtotal);
}
