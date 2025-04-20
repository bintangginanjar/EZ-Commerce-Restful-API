package rest.api.ezcommerce.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.AddressEntity;
import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.OrderResponse;
import rest.api.ezcommerce.model.RegisterOrderRequest;
import rest.api.ezcommerce.model.UpdateOrderRequest;
import rest.api.ezcommerce.repository.AddressRepository;
import rest.api.ezcommerce.repository.OrderRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ValidationService validationService;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.validationService = validationService;
    }

    @Transactional
    public OrderResponse register(Authentication authentication, RegisterOrderRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AddressEntity address = addressRepository.findByUserEntityAndId(user, request.getAddressId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(request.getStatus());
        order.setRemark(request.getRemark());
        order.setUserEntity(user);
        order.setAddressEntity(address);
        orderRepository.save(order);

        return ResponseMapper.ToOrderResponseMapper(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Authentication authentication, String orderId) {                
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        OrderEntity order = orderRepository.findByUserEntityAndOrderId(user, orderId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));;

        return ResponseMapper.ToOrderResponseMapper(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<OrderEntity> orders = orderRepository.findAllByUserEntity(user);

        return ResponseMapper.ToOrderResponseListMapper(orders);
    }

    @Transactional
    public OrderResponse update(Authentication authentication, UpdateOrderRequest request,  String orderId) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        OrderEntity order = orderRepository.findByUserEntityAndOrderId(user, orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (Objects.nonNull(request.getTotalAmount())) {
            order.setTotalAmount(request.getTotalAmount());
        }

        if (Objects.nonNull(request.getStatus())) {
            order.setStatus(request.getStatus());
        }

        if (Objects.nonNull(request.getRemark())) {
            order.setRemark(request.getRemark());
        }

        return ResponseMapper.ToOrderResponseMapper(order);
    }

}
