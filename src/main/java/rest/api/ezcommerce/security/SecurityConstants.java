package rest.api.ezcommerce.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

    @Value("${jwt.expiration}")
    private Integer jwtExpiration;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Integer getJwtExpiration() {
        return jwtExpiration;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

}
