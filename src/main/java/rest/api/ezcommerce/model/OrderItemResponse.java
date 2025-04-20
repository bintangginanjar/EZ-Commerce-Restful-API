package rest.api.ezcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {

    private Integer id;

    private String orderId;

    private Integer productId;

    private String productName;

    private Double productPrice;

    private Integer quantity;

    private Double amount;

}
