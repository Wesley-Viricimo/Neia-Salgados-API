package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
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
    private final AuditingService auditingService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, OrderFactory orderFactory, AuditingService auditingService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.auditingService = auditingService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseDataDTO<OrderResponseDTO> createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.save(orderFactory.createOrder(orderRequestDTO));
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedido realizado com sucesso"));
        return new ResponseDataDTO<>(orderResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }
}
