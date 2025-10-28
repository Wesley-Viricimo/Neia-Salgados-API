package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.neiasalgados.repository.*;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final AdditionalRepository additionalRepository;
    private final UserRepository userRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, AddressRepository addressRepository, OrderItemRepository orderItemRepository, AdditionalRepository additionalRepository, UserRepository userRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
        this.additionalRepository = additionalRepository;
        this.userRepository = userRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }
}
