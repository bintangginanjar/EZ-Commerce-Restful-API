package rest.api.ezcommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rest.api.ezcommerce.model.CartItemResponse;
import rest.api.ezcommerce.model.RegisterCartItemRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.CartItemService;

@RestController
public class CartItemController {

    @Autowired
    CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/carts/items",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CartItemResponse> register(Authentication authentication, 
                                            @RequestBody RegisterCartItemRequest request) {

        CartItemResponse response = cartItemService.register(authentication, request);

        return WebResponse.<CartItemResponse>builder()
                                        .status(true)
                                        .messages("Cart item registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/carts/items",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CartItemResponse>> list(Authentication authentication) {

        List<CartItemResponse> response = cartItemService.list(authentication);

        return WebResponse.<List<CartItemResponse>>builder()
                                        .status(true)
                                        .messages("Cart items fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/carts/items/{itemId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CartItemResponse>> delete(Authentication authentication,
                                                    @PathVariable("itemId") String itemId) {

        cartItemService.delete(authentication, itemId);

        return WebResponse.<List<CartItemResponse>>builder()
                                        .status(true)
                                        .messages("Cart items delete success")                                        
                                        .build();      
    }

}
