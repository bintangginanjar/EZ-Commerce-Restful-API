package rest.api.ezcommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.AddressEntity;
import rest.api.ezcommerce.entity.UserEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {

    Optional<AddressEntity> findFirstByUserEntityAndId(UserEntity user, Integer id);

    List<AddressEntity> findAllByUserEntity(UserEntity user);

    Optional<AddressEntity> findByTitle(String title);

    Optional<AddressEntity> findByUserEntityAndId(UserEntity userEntity, Integer id);

    List<AddressEntity> findAllByUserEntityAndTitle(UserEntity user, String title);

}
