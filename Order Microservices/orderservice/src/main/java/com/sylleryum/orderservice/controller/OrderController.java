package com.sylleryum.orderservice.controller;

import com.sylleryum.orderservice.OrderRepository;
import com.sylleryum.orderservice.client.InventoryClient;
import com.sylleryum.orderservice.dto.OrderDto;
import model.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/order")
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderController(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto) {
        boolean allProductsInStock = orderDto.getOrderLineItemsList().stream()
                .allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode()));

        if (allProductsInStock) {

            Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);
            return "Order placed successfully";
        } else {
            return "Order failed, one of the products in the order is not in stock;";
        }
    }
}
