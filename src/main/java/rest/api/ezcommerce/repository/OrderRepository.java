package rest.api.ezcommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.UserEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer>{

    Optional<OrderEntity> findByOrderId(String orderId);

    Optional<OrderEntity> findByUserEntityAndOrderId(UserEntity userEntity, String orderId);

    Optional<OrderEntity> findByUserEntityAndIdAndOrderId(UserEntity userEntity, Integer id, String orderId);

    List<OrderEntity> findAllByUserEntity(UserEntity userEntity);

}
