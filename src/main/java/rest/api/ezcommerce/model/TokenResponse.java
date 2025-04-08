package rest.api.ezcommerce.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {

    private String email;

    private String token;

    private final String tokenType = "Bearer ";

    private List<String> roles;

}