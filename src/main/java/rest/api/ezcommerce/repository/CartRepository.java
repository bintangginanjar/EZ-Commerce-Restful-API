package rest.api.ezcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.CartEntity;
import rest.api.ezcommerce.entity.UserEntity;

import java.util.Optional;


public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    Optional<CartEntity> findByUserEntity(UserEntity userEntity);

}
