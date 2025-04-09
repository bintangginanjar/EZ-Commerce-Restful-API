package rest.api.ezcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rest.api.ezcommerce.entity.ProfileEntity;
import rest.api.ezcommerce.entity.UserEntity;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer>{

    Optional<ProfileEntity> findByFirstname(String firstname);

    Optional<ProfileEntity> findFirstByUserEntity(UserEntity userEntity);

}
