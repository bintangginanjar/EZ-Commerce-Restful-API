package rest.api.ezcommerce.mapper;

import java.util.List;

import rest.api.ezcommerce.entity.ProfileEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.ProfileResponse;
import rest.api.ezcommerce.model.TokenResponse;
import rest.api.ezcommerce.model.UserResponse;

public class ResponseMapper {

    public static UserResponse ToUserResponseMapper(UserEntity user) {        
        List<String> roles = user.getRoles().stream().map(p -> p.getName()).toList();

        return UserResponse.builder()                
                .email(user.getEmail())
                .role(roles)
                .build();
    }

    public static TokenResponse ToTokenResponseMapper(UserEntity user, String token, List<String> roles) {
        return TokenResponse.builder()
                .email(user.getEmail())
                .token(token)
                .roles(roles)
                .build();

    }

    public static ProfileResponse ToProfileResponseMapper(ProfileEntity profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())                
                .phoneNumber(profile.getPhoneNumber())                                                
                .build();
    }
}
