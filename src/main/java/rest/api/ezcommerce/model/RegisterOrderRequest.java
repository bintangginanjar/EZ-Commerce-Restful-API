package rest.api.ezcommerce.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterOrderRequest {

    @NotNull
    private Integer addressId;

    @NotNull
    private Double totalAmount;

    @NotBlank
    private String status;

    @NotBlank
    private String remark;

}
