package com.example.ddd.order.domain.model;

/**
 * 業務ルール（不変条件）違反を表す例外。
 * ドメイン層の言葉で「何がダメだったか」を伝える。HTTPステータス等は知らない。
 */
public class OrderDomainException extends RuntimeException {

    public OrderDomainException(String message) {
        super(message);
    }
}
