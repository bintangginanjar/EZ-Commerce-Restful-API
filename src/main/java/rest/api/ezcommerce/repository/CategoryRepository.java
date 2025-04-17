package rest.api.ezcommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.UserEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>{

    Optional<CategoryEntity> findFirstByUserEntityAndId(UserEntity user, Integer id);

    Optional<CategoryEntity> findByName(String name);    

    List<CategoryEntity> findAllByUserEntity(UserEntity user);
    
}
