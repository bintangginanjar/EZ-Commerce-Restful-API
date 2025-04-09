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
public class RegisterProfileRequest {

    @NotBlank
    private String firstname;
        
    private String lastname;    
    
    private String phoneNumber;    

}
