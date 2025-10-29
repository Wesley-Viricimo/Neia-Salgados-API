package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.CategoryCreateRequestDTO;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.dto.response.CategoryResponseDTO;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neiasalgados/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseDataDTO<OrderResponseDTO>> create(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        ResponseDataDTO<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
