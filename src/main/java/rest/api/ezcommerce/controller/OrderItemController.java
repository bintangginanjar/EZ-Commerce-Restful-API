package rest.api.ezcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rest.api.ezcommerce.model.OrderItemResponse;
import rest.api.ezcommerce.model.RegisterOrderItemRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.OrderItemService;

@RestController
public class OrderItemController {
    
    @Autowired
    OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/orders/{orderId}/items",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderItemResponse> register(Authentication authentication, 
                                            @RequestBody RegisterOrderItemRequest request,
                                            @PathVariable("orderId") String orderId) {

        OrderItemResponse response = orderItemService.register(authentication, request, orderId);

        return WebResponse.<OrderItemResponse>builder()
                                        .status(true)
                                        .messages("Order item registration success")
                                        .data(response)
                                        .build();      
    }
}
