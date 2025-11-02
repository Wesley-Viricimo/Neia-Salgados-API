package org.neiasalgados.domain.factory;

import org.neiasalgados.domain.dto.OrderAdditionalsResultDTO;
import org.neiasalgados.domain.dto.OrderItemsResultDTO;
import org.neiasalgados.domain.dto.request.OrderAdditionalRequestDTO;
import org.neiasalgados.domain.dto.request.OrderAddressRequestDTO;
import org.neiasalgados.domain.dto.request.OrderItemRequestDTO;
import org.neiasalgados.domain.dto.request.OrderRequestDTO;
import org.neiasalgados.domain.entity.*;
import org.neiasalgados.domain.enums.PaymentMethods;
import org.neiasalgados.domain.enums.TypeOfDelivery;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.repository.AddressRepository;
import org.neiasalgados.repository.AdditionalRepository;
import org.neiasalgados.repository.ProductRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OrderFactory {
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final AdditionalRepository additionalRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public OrderFactory(AddressRepository addressRepository, ProductRepository productRepository, AdditionalRepository additionalRepository, UserRepository userRepository, AuthenticationFacade authenticationFacade) {
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.additionalRepository = additionalRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
    }

    public Order createOrder(OrderRequestDTO dto) {
        User user = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

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
}