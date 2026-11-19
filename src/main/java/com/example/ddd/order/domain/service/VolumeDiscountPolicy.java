package com.example.ddd.order.domain.service;

import com.example.ddd.order.domain.model.Money;

/**
 * 業務ルール：小計が10,000円以上なら10%割引。
 * Springのアノテーションは付けない（ドメイン層はフレームワークに依存させない）。
 * Beanとしての登録は infrastructure.config.DomainConfig で行う。
 */
public class VolumeDiscountPolicy implements DiscountPolicy {

    private static final Money THRESHOLD = Money.yen(10_000);
    private static final int DISCOUNT_PERCENT = 10;

    @Override
    public Money discountFor(Money subtotal) {
        if (subtotal.isGreaterThanOrEqual(THRESHOLD)) {
            return subtotal.percent(DISCOUNT_PERCENT);
        }
        return Money.ZERO;
    }
}
