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

import rest.api.ezcommerce.model.AddressResponse;
import rest.api.ezcommerce.model.RegisterAddressRequest;
import rest.api.ezcommerce.model.UpdateAddressRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.AddressService;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/addresses",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> register(Authentication authentication, 
                                            @RequestBody RegisterAddressRequest request) {

        AddressResponse response = addressService.register(authentication, request);

        return WebResponse.<AddressResponse>builder()
                                        .status(true)
                                        .messages("Address registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/addresses/{addressId}",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(Authentication authentication,
                                            @PathVariable("addressId") String addressId) {

        AddressResponse response = addressService.get(authentication, addressId);

        return WebResponse.<AddressResponse>builder()
                                        .status(true)
                                        .messages("Address fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/addresses/list",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> listByUser(Authentication authentication) {

        List<AddressResponse> response = addressService.list(authentication);

        return WebResponse.<List<AddressResponse>>builder()
                                        .status(true)
                                        .messages("Address fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/addresses",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> listAll() {

        List<AddressResponse> response = addressService.listAll();

        return WebResponse.<List<AddressResponse>>builder()
                                        .status(true)
                                        .messages("Address fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/addresses/{addressId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(Authentication authentication, 
                                            @RequestBody UpdateAddressRequest request,
                                            @PathVariable("addressId") String addressId) {

        request.setId(addressId);

        AddressResponse response = addressService.update(authentication, request, addressId);

        return WebResponse.<AddressResponse>builder()
                                        .status(true)
                                        .messages("Address update success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/addresses/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication,
                                            @PathVariable("addressId") String addressId) {

        addressService.delete(authentication, addressId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Address delete success")                                        
                                        .build();      
    }

}
