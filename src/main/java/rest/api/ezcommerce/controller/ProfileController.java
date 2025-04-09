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

import rest.api.ezcommerce.model.ProfileResponse;
import rest.api.ezcommerce.model.RegisterProfileRequest;
import rest.api.ezcommerce.model.UpdateProfileRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.service.ProfileService;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/profiles",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> register(Authentication authentication, 
                                            @RequestBody RegisterProfileRequest request) {

        ProfileResponse response = profileService.register(authentication, request);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile registration success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/profiles",                
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> get(Authentication authentication) {

        ProfileResponse response = profileService.get(authentication);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile fetching success")
                                        .data(response)
                                        .build();      
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/profiles",        
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileResponse> update(Authentication authentication, 
                                            @RequestBody UpdateProfileRequest request) {

        ProfileResponse response = profileService.update(authentication, request);

        return WebResponse.<ProfileResponse>builder()
                                        .status(true)
                                        .messages("Profile update success")
                                        .data(response)
                                        .build();      
    }
}
