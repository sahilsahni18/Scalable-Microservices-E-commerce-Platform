package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.entity.Inventory;
import com.ecommerce.orderservice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public Inventory getInventory(@PathVariable UUID productId) {
        return inventoryService.getInventory(productId);
    }

    @PostMapping("/{productId}")
    public Inventory createInventory(@PathVariable UUID productId,
                                     @RequestParam int quantity) {
        return inventoryService.createOrUpdateInventory(productId, quantity);
    }

    @PatchMapping("/{productId}/reserve")
    public Inventory reserve(@PathVariable UUID productId,
                             @RequestParam int quantity) {
        return inventoryService.reserveStock(productId, quantity);
    }

    @PatchMapping("/{productId}/release")
    public Inventory release(@PathVariable UUID productId,
                             @RequestParam int quantity) {
        return inventoryService.releaseStock(productId, quantity);
    }
}