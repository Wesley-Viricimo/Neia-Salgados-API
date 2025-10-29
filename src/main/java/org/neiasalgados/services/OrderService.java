package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.OrderAdditionalsResultDTO;
import org.neiasalgados.domain.dto.OrderItemsResultDTO;
import org.neiasalgados.domain.dto.request.OrderAdditionalRequestDTO;
import org.neiasalgados.domain.dto.request.OrderItemRequestDTO;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.OrderResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.*;
import org.neiasalgados.domain.enums.OrderStatus;
import org.neiasalgados.domain.enums.PaymentMethods;
import org.neiasalgados.domain.enums.TypeOfDelivery;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.repository.*;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final AdditionalRepository additionalRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, AddressRepository addressRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository, AdditionalRepository additionalRepository, UserRepository userRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.additionalRepository = additionalRepository;
        this.userRepository = userRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseDataDTO<OrderResponseDTO> createOrder(OrderRequestDTO orderRequestDTO) {
        User userAuthenticated = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

        TypeOfDelivery typeOfDelivery = validateAndGetTypeOfDelivery(orderRequestDTO.getTypeOfDelivery());

        if (typeOfDelivery == TypeOfDelivery.ENTREGA && orderRequestDTO.getAddress() == null)
            throw new DataIntegrityViolationException("Endereço deve ser fornecido para o tipo de entrega 'ENTREGA'");

        PaymentMethods paymentMethod = validateAndGetPaymentMethod(orderRequestDTO.getPaymentMethod());

        Address address = orderRequestDTO.getAddress() != null ?
                this.addressRepository.findById(orderRequestDTO.getAddress().getIdAddress()).orElseThrow(() -> new DataIntegrityViolationException("Endereço com ID " + orderRequestDTO.getAddress().getIdAddress() + " não encontrado")) : null;

        if (address != null && !address.getUser().getIdUser().equals(userAuthenticated.getIdUser()))
            throw new DataIntegrityViolationException("Endereço não pertence ao usuário autenticado");

        OrderItemsResultDTO orderItemsResultDTO = getOrderItems(orderRequestDTO.getOrderItems());
        OrderAdditionalsResultDTO orderAdditionalsResultDTO = getOrderAdditionals(orderRequestDTO.getOrderAdditionals());

        Order order = new Order(
                userAuthenticated,
                address,
                OrderStatus.RECEBIDO,
                paymentMethod,
                typeOfDelivery,
                orderAdditionalsResultDTO.getTotalPrice(),
                orderItemsResultDTO.getTotalPrice(),
                orderItemsResultDTO.getItems(),
                orderAdditionalsResultDTO.getAdditionals()
        );

        orderItemsResultDTO.getItems().forEach(item -> item.setOrder(order));
        orderAdditionalsResultDTO.getAdditionals().forEach(additional -> additional.setOrder(order));
        this.orderRepository.save(order);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Pedido realizado com sucesso"));
        return new ResponseDataDTO<>(orderResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    private OrderItemsResultDTO getOrderItems(List<OrderItemRequestDTO> itemsDTO) {
        var totalPriceHolder = new AtomicReference<Double>(0.0);
        List<OrderItem> items = itemsDTO.stream().map(itemDTO -> {
            Product product = this.productRepository.findById(itemDTO.getIdProduct())
                    .orElseThrow(() -> new DataIntegrityViolationException("Produto com ID " + itemDTO.getIdProduct() + " não encontrado"));

            totalPriceHolder.updateAndGet(currentTotal -> currentTotal + (product.getPrice() * itemDTO.getQuantity()));

            return new OrderItem(
                    product.getDescription(),
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
            Additional additionalEntity = this.additionalRepository.findById(additionalDTO.getIdAdditional())
                    .orElseThrow(() -> new DataIntegrityViolationException("Adicional com ID " + additionalDTO.getIdAdditional() + " não encontrado"));

            totalPriceHolder.updateAndGet(currentTotal -> currentTotal + additionalEntity.getPrice());

            return new OrderAdditional(
                    additionalEntity.getDescription(),
                    additionalEntity.getPrice()
            );
        }).toList();

        return new OrderAdditionalsResultDTO(additionals, totalPriceHolder.get());
    }

    private PaymentMethods validateAndGetPaymentMethod(String paymentMethodStr) {
        String normalizedMethod = paymentMethodStr.toUpperCase();
        boolean isValidMethod = Arrays.stream(PaymentMethods.values())
                .anyMatch(method -> method.name().equals(normalizedMethod));

        if (!isValidMethod)
            throw new DataIntegrityViolationException(String.format("Método de pagamento inválido. Os métodos permitidos são '%s'", Arrays.toString(PaymentMethods.values())));

        return PaymentMethods.valueOf(normalizedMethod);
    }

    private TypeOfDelivery validateAndGetTypeOfDelivery(String typeOfDeliveryStr) {
        String normalizedType = typeOfDeliveryStr.toUpperCase();
        boolean isValidType = Arrays.stream(TypeOfDelivery.values())
                .anyMatch(type -> type.name().equals(normalizedType));

        if (!isValidType)
            throw new DataIntegrityViolationException(String.format("Tipo de entrega inválido. Os tipos permitidos são '%s'", Arrays.toString(TypeOfDelivery.values())));

        return TypeOfDelivery.valueOf(normalizedType);
    }
}
