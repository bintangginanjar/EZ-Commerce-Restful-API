package rest.api.ezcommerce.mapper;

import java.util.List;
import java.util.stream.Collectors;

import rest.api.ezcommerce.entity.AddressEntity;
import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.ProfileEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.AddressResponse;
import rest.api.ezcommerce.model.CategoryResponse;
import rest.api.ezcommerce.model.ProductResponse;
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

    public static AddressResponse ToAddressResponseMapper(AddressEntity address) {
        return AddressResponse.builder()
                .id(address.getId())
                .title(address.getTitle())
                .address(address.getAddress())
                .city(address.getCity())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }

    public static List<AddressResponse> ToAddressResponseListMapper(List<AddressEntity> addresses) {
        return addresses.stream()
                            .map(
                                p -> new AddressResponse(
                                    p.getId(),
                                    p.getTitle(),
                                    p.getAddress(),
                                    p.getCountry(),
                                    p.getCity(),
                                    p.getPostalCode()
                                )).collect(Collectors.toList());
    }

    public static CategoryResponse ToCategoryResponseMapper(CategoryEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static List<CategoryResponse> ToCategoryResponseListMapper(List<CategoryEntity> categories) {
        return categories.stream()
                            .map(
                                p -> new CategoryResponse(
                                    p.getId(),
                                    p.getName()
                                )).collect(Collectors.toList());
    }
    
    public static ProductResponse ToProductResponseMapper(ProductEntity product) {
        return ProductResponse.builder()
                .id(product.getId())
                .category(product.getCategoryEntity().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    public static List<ProductResponse> ToProductResponseListMapper(List<ProductEntity> products) {
        return products.stream()
                            .map(
                                p -> new ProductResponse(
                                    p.getId(),
                                    p.getCategoryEntity().getName(),
                                    p.getName(),
                                    p.getDescription(),
                                    p.getPrice(),
                                    p.getStock()
                                )).collect(Collectors.toList());
    }
}
