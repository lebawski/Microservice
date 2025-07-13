package com.example.inventory_service.service;



import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepo;
import com.example.shared.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private  final InventoryRepo inventoryRepo;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> code){
        return inventoryRepo.findByCodeIn(code).stream()
                .map(inventory -> InventoryResponse.builder()
                        .code(inventory.getCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build()).toList();
    }
    public boolean isInStockSingle(String code) {
        Inventory item = inventoryRepo.findFirstByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + code));

        return item.getQuantity() > 0;
    }

}
