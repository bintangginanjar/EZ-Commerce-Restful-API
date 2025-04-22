package rest.api.ezcommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.CartEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.CartResponse;
import rest.api.ezcommerce.repository.CartRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    ValidationService validationService;

    public CartService(UserRepository userRepository, CartRepository cartRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.validationService = validationService;
    }

    @Transactional
    public CartResponse create(Authentication authentication) {

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (cartRepository.findByUserEntity(user).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already registered");
        }

        CartEntity cart = new CartEntity();
        cart.setUserEntity(user);
        cart.setTotalItems(0);        
        cartRepository.save(cart);

        return ResponseMapper.ToCartResponseMapper(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse get(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CartEntity cart = cartRepository.findByUserEntity(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        return ResponseMapper.ToCartResponseMapper(cart);                    
    }

}
