package com.example.shared.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.criteria.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVehicleDto {
    private Long id;

    private BigDecimal price;
    private Integer quantity;
    private String code;
    private String type;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
