package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.repository.UserRepository;
import com.ecommerce.orderservice.service.OrderService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Order placeOrder(@RequestBody CreateOrderRequest request) {

        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UUID userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return orderService.placeOrder(
                userId,
                request.getProductId(),
                request.getQuantity(),
                request.getIdempotencyKey()
        );
    }

    @GetMapping("/my")
    public List<Order> getMyOrders() {

        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UUID userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable UUID id,
                             @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
}