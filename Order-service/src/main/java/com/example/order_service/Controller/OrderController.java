package com.example.order_service.Controller;

import com.example.order_service.Service.OrderService;
import com.example.order_service.model.Order;
import com.example.shared.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create Order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order created";
    }

    @PostMapping("/simple")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> placeSimpleOrder(@RequestParam String code) {
        try {
            orderService.placeSimpleOrder(code);
            return ResponseEntity.ok("Order placed successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error placing order: " + e.getMessage());
        }
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Update order
    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        orderService.updateOrder(id, orderRequest);
        return new ResponseEntity<>("Order updated", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
