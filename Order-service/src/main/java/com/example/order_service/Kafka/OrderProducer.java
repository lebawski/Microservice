//package com.example.order_service.Kafka;
//
//import com.example.order_service.dto.OrderRequest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OrderProducer {
//
//    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;
//
//    public void sendOrder(OrderRequest orderRequest) {
//        kafkaTemplate.send("order-topic", orderRequest);
//    }
//}
//