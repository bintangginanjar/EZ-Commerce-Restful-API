package rest.api.ezcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import rest.api.ezcommerce.model.CartResponse;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.CartService;

@RestController
public class CartController {

    @Autowired
    CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/carts",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CartResponse> create(Authentication authentication) {

        CartResponse response = cartService.create(authentication);

        return WebResponse.<CartResponse>builder()
                                        .status(true)
                                        .messages("Cart registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/carts",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CartResponse> list(Authentication authentication) {

        CartResponse response = cartService.get(authentication);

        return WebResponse.<CartResponse>builder()
                                        .status(true)
                                        .messages("Cart fetching success")
                                        .data(response)
                                        .build();      
    }
}
