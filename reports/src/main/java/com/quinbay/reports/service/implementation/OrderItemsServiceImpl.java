package com.quinbay.reports.service.implementation;

import com.quinbay.reports.dao.entity.OrderItems;
import com.quinbay.reports.dao.repository.OrderItemsRepository;
import com.quinbay.reports.service.serviceI.OrderItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemsServiceImpl implements OrderItemsService {

    private final OrderItemsRepository orderItemsRepository;

    @Autowired
    public OrderItemsServiceImpl(OrderItemsRepository orderItemsRepository) {
        this.orderItemsRepository = orderItemsRepository;
    }

    @Override
    public List<OrderItems> saveOrderItemList(List<OrderItems> orderItems) {

        for (OrderItems orderItem : orderItems) {

            orderItemsRepository.save(orderItem);
        }

        return orderItems;
    }
    @Override
    public OrderItems saveOrderItem(OrderItems orderItem) {
        return orderItemsRepository.save(orderItem);
    }

    @Override
    public Optional<OrderItems> getOrderItemById(Long id) {
        return orderItemsRepository.findById(id);
    }

    @Override
    public List<OrderItems> getAllOrderItems() {
        return orderItemsRepository.findAll();
    }

    @Override
    public OrderItems updateOrderItem(Long id, OrderItems orderItem) {
        if (orderItemsRepository.existsById(id)) {
            orderItem.setId(id);
            return orderItemsRepository.save(orderItem);
        } else {
            throw new RuntimeException("Order item not found with id " + id);
        }
    }

    @Override
    public void deleteOrderItem(Long id) {
        if (orderItemsRepository.existsById(id)) {
            orderItemsRepository.deleteById(id);
        } else {
            throw new RuntimeException("Order item not found with id " + id);
        }
    }
}