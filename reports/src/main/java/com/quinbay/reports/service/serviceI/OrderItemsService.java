package com.quinbay.reports.service.serviceI;

import com.quinbay.reports.dao.entity.OrderItems;

import java.util.List;
import java.util.Optional;

public interface OrderItemsService {

    OrderItems saveOrderItem(OrderItems orderItem);
    Optional<OrderItems> getOrderItemById(Long id);
    List<OrderItems> getAllOrderItems();
    OrderItems updateOrderItem(Long id, OrderItems orderItem);
    void deleteOrderItem(Long id);
    public List<OrderItems> saveOrderItemList(List<OrderItems> orderItems);
}
