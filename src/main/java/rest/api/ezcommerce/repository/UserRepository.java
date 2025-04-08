package rest.api.ezcommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rest.api.ezcommerce.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findFirstByToken(String token);

}
