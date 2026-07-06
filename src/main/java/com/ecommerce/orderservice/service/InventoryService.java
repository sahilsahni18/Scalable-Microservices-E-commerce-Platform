package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.entity.Inventory;
import com.ecommerce.orderservice.exception.ResourceNotFoundException;
import com.ecommerce.orderservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory getInventory(UUID productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
    }

    @Transactional
    public Inventory reserveStock(UUID productId, int quantity) {
        Inventory inventory = getInventory(productId);

        int available = inventory.getQuantity() - inventory.getReserved();

        if (available < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        inventory.setReserved(inventory.getReserved() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory releaseStock(UUID productId, int quantity) {
        Inventory inventory = getInventory(productId);

        if (inventory.getReserved() < quantity) {
            throw new RuntimeException("Cannot release more than reserved");
        }

        inventory.setReserved(inventory.getReserved() - quantity);
        return inventoryRepository.save(inventory);
    }

    public Inventory createOrUpdateInventory(UUID productId, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        inventory.setReserved(0);
        return inventoryRepository.save(inventory);
    }
}