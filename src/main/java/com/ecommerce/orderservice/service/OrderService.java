package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.entity.*;
import com.ecommerce.orderservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    public OrderService(ProductRepository productRepo,
                        InventoryRepository inventoryRepo,
                        OrderRepository orderRepo,
                        UserRepository userRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Order placeOrder(UUID userId, UUID productId,
                            int quantity, String idempotencyKey) {

        // Idempotency check
        if (idempotencyKey != null) {
            Optional<Order> existing = orderRepo.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Validate product
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate inventory
        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));

        // Stock check (FIXED)
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException(
                    "Not enough stock. Available: " + inventory.getQuantity()
            );
        }

        // Deduct stock
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepo.save(inventory);

        // Calculate total
        BigDecimal totalAmount = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING"); 
        order.setIdempotencyKey(idempotencyKey);
        order.setTotalAmount(totalAmount);

        return orderRepo.save(order);
    }

    @Transactional
    public Order updateStatus(UUID orderId, String newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);
        return orderRepo.save(order);
    }

    public List<Order> getOrdersByUser(UUID userId) {
        return orderRepo.findByUserId(userId);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
}