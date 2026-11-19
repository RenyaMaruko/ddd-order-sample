package com.example.ddd.order.infrastructure.config;

import com.example.ddd.order.domain.service.DiscountPolicy;
import com.example.ddd.order.domain.service.VolumeDiscountPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * フレームワークに依存させたくないドメインクラスを、Springのビーンとして登録する場所。
 * ドメイン層に @Component を書かず、外側（インフラ）で組み立てる。
 */
@Configuration
public class DomainConfig {

    @Bean
    public DiscountPolicy discountPolicy() {
        return new VolumeDiscountPolicy();
    }
}
