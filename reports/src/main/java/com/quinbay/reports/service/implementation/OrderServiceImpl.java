package com.quinbay.reports.service.implementation;

import com.quinbay.reports.dao.entity.Order;
import com.quinbay.reports.dao.entity.OrderItems;
import com.quinbay.reports.dao.repository.OrderRepository;
import com.quinbay.reports.dto.OrderDto;
import com.quinbay.reports.dto.OrderItemDto;
import com.quinbay.reports.exceptions.ResourceNotFoundException;
import com.quinbay.reports.service.serviceI.OrderItemsService;
import com.quinbay.reports.service.serviceI.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;

    private final OrderItemsService orderItemsService;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return null;
    }

    @KafkaListener(topics = "order.service", groupId = "group-1", containerFactory = "kafkaListenerContainerFactory")
    public Order createOrder(OrderDto order) {
        Order newOrder = new Order();
        List<OrderItems> orderItems = new ArrayList<>();

        for(OrderItemDto itemDto : order.getOrderItems()) {

            OrderItems orderItem = new OrderItems();
            orderItem.setCategoryCode(itemDto.getCategoryCode());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());
            orderItem.setName(itemDto.getName());
            orderItem.setCode(itemDto.getCode());
            orderItem.setSellerId(itemDto.getSellerId());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
        }

        newOrder.setCode(order.getCode());
        newOrder.setTotalAmount(order.getTotalPrice());
        newOrder.setCustomerId(order.getCustomerId());
        newOrder.setTotalQuantity(order.getTotalQuantity());
        newOrder.setNumberOfItems(order.getNumberOfItems());
        newOrder.setOrderItems(orderItems);
        newOrder.setOrderedOn(order.getOrderedOn());
        return orderRepository.save(newOrder);
    }

    @Override
    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setCustomerId(order.getCustomerId());
        existingOrder.setTotalQuantity(order.getTotalQuantity());
        existingOrder.setOrderedOn(order.getOrderedOn());
        existingOrder.setOrderItems(order.getOrderItems());
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(existingOrder);
    }

    @Override
    public byte[] generateReport() {

        List<Order> orders = getAllOrders();
        ByteArrayOutputStream outputStream = null;

        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Orders Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Order ID", "Code", "Total Amount", "Number of Items", "Customer ID", "Total Quantity", "Ordered On", "Item Code", "Item Name", "Item Quantity", "Item Price", "Seller ID", "Category Code"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Order order : orders) {
                for (OrderItems item : order.getOrderItems()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(order.getId());
                    row.createCell(1).setCellValue(order.getCode());
                    row.createCell(2).setCellValue(order.getTotalAmount());
                    row.createCell(3).setCellValue(order.getNumberOfItems());
                    row.createCell(4).setCellValue(order.getCustomerId());
                    row.createCell(5).setCellValue(order.getTotalQuantity());
                    row.createCell(6).setCellValue(order.getOrderedOn().toString());
                    row.createCell(7).setCellValue(item.getCode());
                    row.createCell(8).setCellValue(item.getName());
                    row.createCell(9).setCellValue(item.getQuantity());
                    row.createCell(10).setCellValue(item.getPrice());
                    row.createCell(11).setCellValue(item.getSellerId());
                    row.createCell(12).setCellValue(item.getCategoryCode());
                }
            }

            outputStream = new ByteArrayOutputStream();


            workbook.write(outputStream);
            workbook.close();
        }
        catch (IOException e) {

            log.warn(e.getMessage());
        }

        return outputStream.toByteArray();
    }
}