package com.example.ddd.order.presentation;

import com.example.ddd.order.application.OrderApplicationService;
import com.example.ddd.order.application.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【プレゼンテーション層】HTTPの入出力だけを担当する。
 * 業務ロジックは持たず、アプリケーションサービスに委譲するだけ。
 * ドメイン層のクラス（Order など）は import しない点に注目。
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService service;

    public OrderController(OrderApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto create() {
        return service.createOrder();
    }

    @GetMapping("/{id}")
    public OrderDto get(@PathVariable String id) {
        return service.find(id);
    }

    @PostMapping("/{id}/items")
    public OrderDto addItem(@PathVariable String id, @RequestBody AddItemRequest req) {
        return service.addItem(id, req.getProductId(), req.getProductName(), req.getUnitPrice(), req.getQuantity());
    }

    @DeleteMapping("/{id}/items/{productId}")
    public OrderDto removeItem(@PathVariable String id, @PathVariable String productId) {
        return service.removeItem(id, productId);
    }

    @PostMapping("/{id}/confirm")
    public OrderDto confirm(@PathVariable String id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/ship")
    public OrderDto ship(@PathVariable String id) {
        return service.ship(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderDto cancel(@PathVariable String id) {
        return service.cancel(id);
    }
}
