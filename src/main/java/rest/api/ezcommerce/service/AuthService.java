package rest.api.ezcommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.LoginUserRequest;
import rest.api.ezcommerce.model.TokenResponse;
import rest.api.ezcommerce.repository.UserRepository;
import rest.api.ezcommerce.security.CustomUserDetailService;
import rest.api.ezcommerce.security.JwtUtil;
import rest.api.ezcommerce.security.SecurityConstants;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    ValidationService validationService;

    public AuthService(AuthenticationManager authenticationManager, 
                        ValidationService validationService,
                        JwtUtil jwtUtil, UserRepository userRepository) {        
        this.authenticationManager = authenticationManager;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        try {
            Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), request.getPassword())
                                );
                                            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailService.loadUserByUsername(request.getEmail());

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            
            UserEntity user = userRepository.findByEmail(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            String token = jwtUtil.generateToken(authentication);

            user.setToken(token);
            user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
            userRepository.save(user);
            
            return ResponseMapper.ToTokenResponseMapper(user, token, roles);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
        }        
    }

    @Transactional
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        
        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username not found");
        }

    }

}