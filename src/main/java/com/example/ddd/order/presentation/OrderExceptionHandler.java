package com.example.ddd.order.presentation;

import com.example.ddd.order.application.OrderNotFoundException;
import com.example.ddd.order.domain.model.OrderDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

/**
 * ドメインの例外をHTTPステータスに翻訳する。
 * 「業務ルール違反 = 422」「存在しない = 404」という対応づけはプレゼンテーション層の責務。
 */
@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Map<String, String>> handleDomain(OrderDomainException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body(e));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(e));
    }

    private Map<String, String> body(RuntimeException e) {
        return Collections.singletonMap("message", e.getMessage());
    }
}
