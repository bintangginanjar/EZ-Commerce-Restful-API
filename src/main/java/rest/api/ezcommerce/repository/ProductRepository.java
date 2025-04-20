package rest.api.ezcommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.UserEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByUserEntityAndName(UserEntity user, String name);

    List<ProductEntity> findAllByUserEntityAndAndName(UserEntity user, String name);

    Optional<ProductEntity> findFirstByUserEntityAndId(UserEntity user, Integer id);

    Optional<ProductEntity> findFirstByCategoryEntityAndId(CategoryEntity category, Integer id);

    List<ProductEntity> findAllByUserEntity(UserEntity user);

    Optional<ProductEntity> findFirstById(Integer id);

}
