package rest.api.ezcommerce.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.repository.UserRepository;

@Component
public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Autowired
    private UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currDate = new Date();
        Date expDate = new Date(System.currentTimeMillis() + SecurityConstants.JWTexpiration);

        String token = Jwts.builder()
                        .setSubject(email)
                        .setIssuedAt(currDate)
                        .setExpiration(expDate)
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();

        return token;
    }

    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

                return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect", e.fillInStackTrace());
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && (bearerToken.startsWith("Bearer "))) {
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;
    }    

    public Boolean isTokenExpired(String token) {
        UserEntity user = userRepository.findFirstByToken(token)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access")
        );

        if (user.getTokenExpiredAt() < System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

}