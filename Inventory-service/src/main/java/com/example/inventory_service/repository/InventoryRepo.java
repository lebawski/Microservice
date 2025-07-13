package com.example.inventory_service.repository;

import com.example.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepo extends JpaRepository<Inventory , Long> {
    List<Inventory> findByCodeIn(List<String> code);
    @Query("SELECT i FROM Inventory i WHERE i.code = :code ORDER BY i.id DESC")
    Optional<Inventory> findFirstByCode(@Param("code") String code);
    Optional<Inventory> findByCode(String code);
}
