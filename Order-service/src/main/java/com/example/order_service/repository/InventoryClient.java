package com.example.order_service.repository;

import com.example.shared.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/inventory/{code}")
    boolean isInStock(@PathVariable String code);
    @GetMapping("/inventory")
    List<InventoryResponse> isAllInStock(@RequestParam("code") List<String> codes);
}
