package rest.api.ezcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rest.api.ezcommerce.model.LoginUserRequest;
import rest.api.ezcommerce.model.TokenResponse;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
        path = "/api/auth/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        
        return WebResponse.<TokenResponse>builder()
                                        .status(true)
                                        .messages("Login success")
                                        .data(tokenResponse)
                                        .build();
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/auth/logout",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(authentication, request, response);
        
        return WebResponse.<String>builder()
                                            .status(true)
                                            .messages("User logout successfully")                                            
                                            .build();
    }

}
