package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.dto.request.UpdateOrderStatusRequestDTO;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neiasalgados/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @GetMapping
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<OrderResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "isPending", required = false) Boolean isPending
    ) {
        var dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "id_order"));
        return ResponseEntity.ok(orderService.findAll(userName, isPending, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<OrderResponseDTO>>> findByUser(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "desc") String direction
    ) {
        var dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "idOrder"));
        return ResponseEntity.ok(orderService.findByUser(pageable));
    }

    @PostMapping
    public ResponseEntity<ResponseDataDTO<OrderResponseDTO>> create(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        ResponseDataDTO<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @PatchMapping("/update-status")
    public ResponseEntity<ResponseDataDTO<OrderResponseDTO>> updateStatus(@Valid @RequestBody UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        ResponseDataDTO<OrderResponseDTO> response = orderService.updateOrderStatus(updateOrderStatusRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cancel/{idOrder}")
    public ResponseEntity<ResponseDataDTO<OrderResponseDTO>> cancelOrder(@PathVariable Long idOrder) {
        ResponseDataDTO<OrderResponseDTO> response = orderService.cancelOrder(idOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
