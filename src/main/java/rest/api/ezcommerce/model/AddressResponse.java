package rest.api.ezcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {

    private Integer id;

    private String title;

    private String address;

    private String country;

    private String city;
    
    private String postalCode;

}
