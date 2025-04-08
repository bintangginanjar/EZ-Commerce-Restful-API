package rest.api.ezcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import rest.api.ezcommerce.model.RegisterUserRequest;
import rest.api.ezcommerce.model.UpdateUserRequest;
import rest.api.ezcommerce.model.UserResponse;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.UserService;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
        path = "/api/users",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> create(@RequestBody RegisterUserRequest request) {
        UserResponse response = userService.register(request);

        return WebResponse.<UserResponse>builder()
                                        .status(true)
                                        .messages("User registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/users",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )    
    public WebResponse<UserResponse> get(Authentication authentication) {
        log.debug("GET CURRENT USER");
        UserResponse response = userService.get(authentication);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User fetching success")
                                            .data(response)
                                            .build();
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(Authentication authentication, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(authentication, request);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User password successfully updated")
                                            .data(response)
                                            .build();
    }
}
