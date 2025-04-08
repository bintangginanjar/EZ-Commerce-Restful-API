package rest.api.ezcommerce.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 128)        
    private String email;
    
    @NotBlank
    @Size(max = 128)        
    private String password;

    @NotBlank
    @Size(max = 16)
    private String role;

}
