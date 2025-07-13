package com.example.inventory_service.controller;

import com.example.inventory_service.service.InventoryService;
import com.example.shared.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String>code){
        return inventoryService.isInStock(code);
    }
    @GetMapping("/{code}")
    public ResponseEntity<?> isInStock(@PathVariable String code) {
        try {
            boolean inStock = inventoryService.isInStockSingle(code);
            return ResponseEntity.ok(inStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking inventory: " + e.getMessage());
        }
    }
}
