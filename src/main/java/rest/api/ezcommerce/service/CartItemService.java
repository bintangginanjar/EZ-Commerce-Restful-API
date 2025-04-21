package rest.api.ezcommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.CartEntity;
import rest.api.ezcommerce.entity.CartItemEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.CartItemResponse;
import rest.api.ezcommerce.model.RegisterCartItemRequest;
import rest.api.ezcommerce.repository.CartItemRepository;
import rest.api.ezcommerce.repository.CartRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class CartItemService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ValidationService validationService;

    public CartItemService(UserRepository userRepository, CartRepository cartRepository,
            CartItemRepository cartItemRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.validationService = validationService;
    }

    @Transactional
    public CartItemResponse register(Authentication authentication, RegisterCartItemRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CartEntity cart = cartRepository.findByUserEntity(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        ProductEntity product = productRepository.findFirstById(request.getIdProduct())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return ResponseMapper.ToCartItemResponseMapper(item);
    }

    @Transactional
    public List<CartItemResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CartEntity cart = cartRepository.findByUserEntity(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found")); 

        List<CartItemEntity> items = cartItemRepository.findAllByCartEntity(cart);

        return ResponseMapper.ToCartItemListResponseMapper(items);
    }

}
