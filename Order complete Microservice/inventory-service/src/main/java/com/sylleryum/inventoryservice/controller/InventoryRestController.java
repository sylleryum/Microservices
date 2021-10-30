package com.sylleryum.inventoryservice.controller;

import com.sylleryum.inventoryservice.model.Inventory;
import com.sylleryum.inventoryservice.repository.InventoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final InventoryRepository inventoryRepository;

    public InventoryRestController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/{skuCode")
    Boolean isInStock(@PathVariable String skuCode){
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(()->new RuntimeException("Cannot find product by skuCode "+skuCode));
        return inventory.getStock() > 0;
    }
}
