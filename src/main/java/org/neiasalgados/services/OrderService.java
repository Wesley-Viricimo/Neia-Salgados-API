package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.dto.request.UpdateOrderStatusRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.*;
import org.neiasalgados.domain.factory.OrderFactory;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.*;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderFactory orderFactory;
    private final AuthenticationFacade authenticationFacade;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, OrderFactory orderFactory, AuthenticationFacade authenticationFacade) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderFactory = orderFactory;
        this.authenticationFacade = authenticationFacade;
    }

    public ResponseDataDTO<PageResponseDTO<OrderResponseDTO>> findAll(String userName, Boolean isPending, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllWithFilters(userName, isPending, pageable);
        Page<OrderResponseDTO> orderDTOPage = orderPage.map(OrderResponseDTO::new);
        PageResponseDTO<OrderResponseDTO> pageResponse = new PageResponseDTO<>(orderDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedidos listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    public ResponseDataDTO<PageResponseDTO<OrderResponseDTO>> findByUser(Pageable pageable) {
        User user = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado"));

        Page<Order> orderPage = orderRepository.findOrderByUser(user, pageable);
        Page<OrderResponseDTO> orderDTOPage = orderPage.map(OrderResponseDTO::new);
        PageResponseDTO<OrderResponseDTO> pageResponse = new PageResponseDTO<>(orderDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedidos listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
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

    @Transactional
    public ResponseDataDTO<OrderResponseDTO> cancelOrder(Long orderId) {
        Order order = orderRepository.save(orderFactory.cancelOrder(orderId));
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedido cancelado com sucesso"));
        return new ResponseDataDTO<>(orderResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }
}
