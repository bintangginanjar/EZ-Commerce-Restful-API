package rest.api.ezcommerce.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterAddressRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String address;

    @NotBlank
    private String country;

    @NotBlank
    private String city;
    
    @NotBlank
    private String postalCode;

}
