package rest.api.ezcommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {

    List<OrderItemEntity> findAllByOrderEntity(OrderEntity orderEntity);

}
