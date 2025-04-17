package rest.api.ezcommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rest.api.ezcommerce.model.ProductResponse;
import rest.api.ezcommerce.model.RegisterProductRequest;
import rest.api.ezcommerce.model.UpdateProductRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.ProductService;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/categories/{categoryId}/products",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> register(Authentication authentication, 
                                            @RequestBody RegisterProductRequest request,
                                            @PathVariable("categoryId") String categoryId) {

        ProductResponse response = productService.register(authentication, request, categoryId);

        return WebResponse.<ProductResponse>builder()
                                        .status(true)
                                        .messages("Product registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/categories/{categoryId}/products/{productId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> get(Authentication authentication,                                             
                                            @PathVariable("categoryId") String categoryId,
                                            @PathVariable("productId") String productId) {

        ProductResponse response = productService.get(authentication, categoryId, productId);

        return WebResponse.<ProductResponse>builder()
                                        .status(true)
                                        .messages("Product fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/products/list",       
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductResponse>> listByUser(Authentication authentication) {

        List<ProductResponse> response = productService.list(authentication);

        return WebResponse.<List<ProductResponse>>builder()
                                        .status(true)
                                        .messages("Product fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/products",       
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductResponse>> listAll() {

        List<ProductResponse> response = productService.listAll();

        return WebResponse.<List<ProductResponse>>builder()
                                        .status(true)
                                        .messages("Product fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/categories/{categoryId}/products/{productId}",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> update(Authentication authentication, 
                                            @RequestBody UpdateProductRequest request,
                                            @PathVariable("categoryId") String categoryId,
                                            @PathVariable("productId") String productId) {

        ProductResponse response = productService.update(authentication, request, categoryId, productId);

        return WebResponse.<ProductResponse>builder()
                                        .status(true)
                                        .messages("Product update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/categories/{categoryId}/products/{productId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication,
                                        @PathVariable("categoryId") String categoryId,
                                        @PathVariable("productId") String productId) {

        productService.delete(authentication, categoryId, productId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Product delete success")                                        
                                        .build();      
    }

}
