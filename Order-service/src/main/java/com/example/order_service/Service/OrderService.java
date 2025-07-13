package com.example.order_service.Service;

import com.example.order_service.dto.InventoryResponse;
import com.example.order_service.model.Order;
import com.example.order_service.model.VehicleOrder;
import com.example.order_service.repository.InventoryClient;
import com.example.order_service.repository.OrderRepo;
import com.example.shared.dto.OrderRequest;
import com.example.shared.dto.OrderVehicleDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepo orderRepo;
    private final WebClient.Builder webClientBuilder;
    private final InventoryClient inventoryClient;
//    private final com.example.order_service.kafka.OrderProducer orderProducer;


    @CircuitBreaker(name = "InventoryService", fallbackMethod = "inventoryFallback")
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderVehicleDto> vehicleDtoList = orderRequest.getOrderVehicleDtoList();
        if (vehicleDtoList == null) {
            vehicleDtoList = List.of();
        }

        List<VehicleOrder> vehicleOrderList = vehicleDtoList.stream()
                .map(dto -> mapToDto(dto, order))
                .toList();

        order.setVehicleOrderList(vehicleOrderList);

        List<String> codes = order.getVehicleOrderList().stream()
                .map(VehicleOrder::getCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/inventory", uriBuilder -> uriBuilder.queryParam("code", codes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean result = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::getIsInStock);

        if (result) {
            orderRepo.save(order);
        } else {
            throw new IllegalArgumentException("One or more vehicles are not in stock");
        }
    }

    public void inventoryFallback(OrderRequest orderRequest, Throwable throwable) {
        throw new IllegalStateException("Inventory service unavailable, please try again later");
    }

    public void placeSimpleOrder(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }

        try {
            Boolean inStock = inventoryClient.isInStock(code);

            if (inStock == null) {
                throw new IllegalStateException("Inventory service returned invalid response");
            }

            if (inStock) {
            } else {
                throw new IllegalStateException("Product " + code + " not in stock");
            }
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Product not found: " + code);
        } catch (FeignException e) {
            throw new IllegalStateException("Inventory service error: " + e.getMessage());
        }
    }
//    public void placeOrderWithKafka(OrderRequest orderRequest) {
//        validateOrderRequest(orderRequest);
//        Order order = mapToOrder(orderRequest);
//        orderRepo.save(order);
//        orderProducer. sendOrder(orderRequest);
//    }


    private VehicleOrder mapToDto(OrderVehicleDto orderVehicleDto, Order order) {
        VehicleOrder vehicleOrder = new VehicleOrder();
        vehicleOrder.setPrice(orderVehicleDto.getPrice());
        vehicleOrder.setQuantity(orderVehicleDto.getQuantity());
        vehicleOrder.setType(orderVehicleDto.getType());
        vehicleOrder.setCode(orderVehicleDto.getCode());
        vehicleOrder.setOrder(order);
        return vehicleOrder;
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + id + " not found"));
    }

    // Update order (replaces vehicle orders list)
    public void updateOrder(Long id, OrderRequest orderRequest) {
        Order existingOrder = getOrderById(id);

        List<VehicleOrder> updatedVehicleOrders = orderRequest.getOrderVehicleDtoList().stream()
                .map(dto -> mapToDto(dto, existingOrder))
                .toList();

        existingOrder.setVehicleOrderList(updatedVehicleOrders);

        orderRepo.save(existingOrder);
    }

    // Delete order
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepo.delete(order);
    }
}
