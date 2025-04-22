package rest.api.ezcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.CartEntity;
import rest.api.ezcommerce.entity.CartItemEntity;
import java.util.List;
import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer> {

    List<CartItemEntity> findAllByCartEntity(CartEntity cartEntity);

    Optional<CartItemEntity> findFirstByCartEntityAndId(CartEntity cartEntity, Integer id);

}
