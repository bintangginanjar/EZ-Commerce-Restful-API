package rest.api.ezcommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.OrderItemEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.OrderItemResponse;
import rest.api.ezcommerce.model.RegisterOrderItemRequest;
import rest.api.ezcommerce.repository.OrderItemRepository;
import rest.api.ezcommerce.repository.OrderRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class OrderItemService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ValidationService validationService;

    public OrderItemService(UserRepository userRepository, OrderRepository orderRepository,
            ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderItemResponse register(Authentication authentication, RegisterOrderItemRequest request, String strOrderId) {
        validationService.validate(request);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        OrderEntity order = orderRepository.findByUserEntityAndOrderId(user, strOrderId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));                                 

        ProductEntity product = productRepository.findFirstById(request.getIdProduct())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderItemEntity item = new OrderItemEntity();
        item.setOrderEntity(order);
        item.setProductEntity(product);
        item.setQuantity(request.getQuantity());
        item.setAmount(request.getAmount());
        orderItemRepository.save(item);

        return ResponseMapper.ToOrderItemResponseMapper(item);
    }    
}
