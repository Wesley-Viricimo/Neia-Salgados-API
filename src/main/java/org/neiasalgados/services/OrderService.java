package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.dto.request.UpdateOrderStatusRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.*;
import org.neiasalgados.domain.factory.OrderFactory;
import org.neiasalgados.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;

    public OrderService(OrderRepository orderRepository, OrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
    }

    @Transactional
    public ResponseDataDTO<OrderResponseDTO> createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.save(orderFactory.createOrder(orderRequestDTO));
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedido realizado com sucesso"));
        return new ResponseDataDTO<>(orderResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<OrderResponseDTO> updateOrderStatus(UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        Order order = orderRepository.save(orderFactory.updateOrderStatus(updateOrderStatusRequestDTO));
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Status do pedido atualizado com sucesso"));
        return new ResponseDataDTO<>(orderResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }
}
