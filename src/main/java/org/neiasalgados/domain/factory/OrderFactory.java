package org.neiasalgados.domain.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.OrderAdditionalsResultDTO;
import org.neiasalgados.domain.dto.OrderItemsResultDTO;
import org.neiasalgados.domain.dto.request.*;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.entity.*;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.domain.enums.OrderStatus;
import org.neiasalgados.domain.enums.PaymentMethods;
import org.neiasalgados.domain.enums.TypeOfDelivery;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.*;
import org.neiasalgados.security.AuthenticationFacade;
import org.neiasalgados.services.AuditingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OrderFactory {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final AdditionalRepository additionalRepository;
    private final UserRepository userRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public OrderFactory(OrderRepository orderRepository, AddressRepository addressRepository, ProductRepository productRepository, AdditionalRepository additionalRepository, UserRepository userRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.additionalRepository = additionalRepository;
        this.userRepository = userRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }

    public Order createOrder(OrderRequestDTO dto) {
        User user = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado"));

        LocalDateTime twentyMinutesAgo = LocalDateTime.now().minusMinutes(20);
        if (orderRepository.hasRecentCanceledOrder(user.getIdUser(), twentyMinutesAgo)) {
            throw new DataIntegrityViolationException("Não é possível criar um novo pedido. Você possui um pedido cancelado nos últimos 20 minutos. " +
                            "Por favor, aguarde antes de realizar um novo pedido."
            );
        }

        TypeOfDelivery typeOfDelivery = validateAndGetTypeOfDelivery(dto.getTypeOfDelivery());
        PaymentMethods paymentMethod = validateAndGetPaymentMethod(dto.getPaymentMethod());

        Address address = dto.getAddress() != null && typeOfDelivery == TypeOfDelivery.ENTREGA ? validateAndGetAddress(dto.getAddress()) : null;

        OrderItemsResultDTO itemsResult = getOrderItems(dto.getOrderItems());
        OrderAdditionalsResultDTO additionalsResult = getOrderAdditionals(dto.getOrderAdditionals());

        Double totalPrice = itemsResult.getTotalPrice() + additionalsResult.getTotalPrice();

        Order order = new Order(
                user,
                address,
                paymentMethod,
                typeOfDelivery,
                additionalsResult.getTotalPrice(),
                totalPrice,
                itemsResult.getItems(),
                additionalsResult.getAdditionals()
        );

        linkOrderItems(order);
        return order;
    }

    public Order updateOrderStatus(UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        User userAuthenticated = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado"));

        Order order = orderRepository.findById(updateOrderStatusRequestDTO.getIdOrder())
                .orElseThrow(() -> new NotFoundException("Pedido com ID " + updateOrderStatusRequestDTO.getIdOrder() + " não encontrado"));

        if (order.getOrderStatus() == OrderStatus.ENTREGUE || order.getOrderStatus() == OrderStatus.CANCELADO)
            throw new DataIntegrityViolationException("Não é possível alterar o status de um pedido que já foi entregue ou cancelado");

        OrderStatus newStatus = validateAndGetOrderStatus(updateOrderStatusRequestDTO.getOrderStatus());

        OrderResponseDTO originalOrder = new OrderResponseDTO(order);
        order.setOrderStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if (newStatus == OrderStatus.ENTREGUE)
            order.setDeliveryDate(LocalDateTime.now());

        try {
            String originalValue = objectMapper.writeValueAsString(originalOrder);
            String newValue = objectMapper.writeValueAsString(new OrderResponseDTO(order));

            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    userAuthenticated.getIdUser(),
                    "ALTERAÇÃO DO STATUS DO PEDIDO",
                    "PEDIDO",
                    order.getIdOrder(),
                    originalValue,
                    newValue,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        return order;
    }

    public Order cancelOrder(Long idOrder) {
        User userAuthenticated = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado"));

        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new NotFoundException("Pedido com ID " + idOrder + " não encontrado"));

        if (order.getOrderStatus() == OrderStatus.ENTREGUE || order.getOrderStatus() == OrderStatus.CANCELADO)
            throw new DataIntegrityViolationException("Não é possível cancelar um pedido que já foi entregue ou cancelado");

        if (!userAuthenticated.getIdUser().equals(order.getUser().getIdUser()))
            throw new DataIntegrityViolationException("O pedido só pode ser cancelado pelo próprio usuário que o realizou");

        order.setOrderStatus(OrderStatus.CANCELADO);
        order.setUpdatedAt(LocalDateTime.now());

        return order;
    }

    private void linkOrderItems(Order order) {
        order.getItems().forEach(item -> item.setOrder(order));
        order.getAdditionals().forEach(additional -> additional.setOrder(order));
    }

    private Address validateAndGetAddress(OrderAddressRequestDTO addressDTO) {
        return addressRepository.findById(addressDTO.getIdAddress())
                .orElseThrow(() -> new DataIntegrityViolationException("Endereço com ID " + addressDTO.getIdAddress() + " não encontrado"));
    }

    private OrderItemsResultDTO getOrderItems(List<OrderItemRequestDTO> itemsDTO) {
        var totalPriceHolder = new AtomicReference<Double>(0.0);
        List<OrderItem> items = itemsDTO.stream().map(itemDTO -> {
            Product product = productRepository.findById(itemDTO.getIdProduct())
                    .orElseThrow(() -> new DataIntegrityViolationException("Produto com ID " + itemDTO.getIdProduct() + " não encontrado"));

            totalPriceHolder.updateAndGet(currentTotal -> currentTotal + (product.getPrice() * itemDTO.getQuantity()));

            return new OrderItem(
                    product.getTitle(),
                    product.getPrice(),
                    itemDTO.getQuantity(),
                    itemDTO.getComment()
            );
        }).toList();

        return new OrderItemsResultDTO(items, totalPriceHolder.get());
    }

    private OrderAdditionalsResultDTO getOrderAdditionals(List<OrderAdditionalRequestDTO> additionalsDTO) {
        var totalPriceHolder = new AtomicReference<Double>(0.0);
        List<OrderAdditional> additionals = additionalsDTO.stream().map(additionalDTO -> {
            Additional additionalEntity = additionalRepository.findById(additionalDTO.getIdAdditional())
                    .orElseThrow(() -> new DataIntegrityViolationException(String.format("Adicional com ID '%s' não encontrado",  additionalDTO.getIdAdditional())));

            totalPriceHolder.updateAndGet(currentTotal ->
                    currentTotal + additionalEntity.getPrice());

            return new OrderAdditional(
                    additionalEntity.getDescription(),
                    additionalEntity.getPrice()
            );
        }).toList();

        return new OrderAdditionalsResultDTO(additionals, totalPriceHolder.get());
    }

    private PaymentMethods validateAndGetPaymentMethod(String paymentMethodStr) {
        String normalizedMethod = paymentMethodStr.toUpperCase();
        boolean isValidMethod = Arrays.stream(PaymentMethods.values()).anyMatch(method -> method.name().equals(normalizedMethod));

        if (!isValidMethod)
            throw new DataIntegrityViolationException(String.format("Método de pagamento inválido. Os métodos permitidos são '%s'", Arrays.toString(PaymentMethods.values())));

        return PaymentMethods.valueOf(normalizedMethod);
    }

    private TypeOfDelivery validateAndGetTypeOfDelivery(String typeOfDeliveryStr) {
        String normalizedType = typeOfDeliveryStr.toUpperCase();
        boolean isValidType = Arrays.stream(TypeOfDelivery.values())
                .anyMatch(type -> type.name().equals(normalizedType));

        if (!isValidType)
            throw new DataIntegrityViolationException(String.format("Tipo de entrega inválido. Os tipos permitidos são '%s'",  Arrays.toString(TypeOfDelivery.values())));

        return TypeOfDelivery.valueOf(normalizedType);
    }

    private OrderStatus validateAndGetOrderStatus(String orderStatusStr) {
        String normalizedStatus = orderStatusStr.toUpperCase();
        boolean isValidStatus = Arrays.stream(OrderStatus.values())
                .anyMatch(status -> status.name().equals(normalizedStatus));

        if (!isValidStatus)
            throw new DataIntegrityViolationException(String.format("Status de pedido inválido. Os status permitidos são '%s'",  Arrays.toString(OrderStatus.values())));

        return OrderStatus.valueOf(normalizedStatus);
    }
}