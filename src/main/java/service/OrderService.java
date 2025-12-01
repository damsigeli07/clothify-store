package service;

import dto.OrderDto;
import entity.Order;
import repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository orderRepository = new OrderRepository();

    public void createOrder(OrderDto dto) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(dto.getTotalAmount());
        order.setCustomerName(dto.getCustomerName());
        order.setStatus("COMPLETED");

        orderRepository.save(order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getTodayOrders() {
        return orderRepository.findTodayOrders().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Double getTodayTotalSales() {
        return orderRepository.findTodayOrders().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    private OrderDto convertToDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getCustomerName(),
                order.getStatus()
        );
    }
}