package rest.api.ezcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {

    private Integer id;

    private String firstname;
    
    private String lastname;    
    
    private String phoneNumber;

}
