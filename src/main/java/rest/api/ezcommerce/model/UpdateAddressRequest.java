package rest.api.ezcommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressRequest {

    @NotBlank
    @JsonIgnore
    private String id;

    private String title;

    private String address;

    private String country;

    private String city;
    
    private String postalCode;

}
