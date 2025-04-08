package rest.api.ezcommerce.mapper;

import java.util.List;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.UserResponse;

public class ResponseMapper {

    public static UserResponse ToUserResponseMapper(UserEntity user) {        
        List<String> roles = user.getRoles().stream().map(p -> p.getName()).toList();

        return UserResponse.builder()                
                .email(user.getEmail())
                .role(roles)
                .build();
    }
}
