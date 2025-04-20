package rest.api.ezcommerce.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterOrderItemRequest {

    @NotNull
    private Integer idProduct;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double amount;

}
