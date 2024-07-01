package com.quinbay.reports.service.serviceI;

import com.quinbay.reports.dao.entity.Order;
import com.quinbay.reports.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createOrder(OrderDto order);

    Order updateOrder(Long id, Order order);

    void deleteOrder(Long id);

    byte[] generateReport();
}
