package rest.api.ezcommerce.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rest.api.ezcommerce.entity.AddressEntity;
import rest.api.ezcommerce.entity.RoleEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.AddressResponse;
import rest.api.ezcommerce.model.RegisterAddressRequest;
import rest.api.ezcommerce.model.UpdateAddressRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.repository.AddressRepository;
import rest.api.ezcommerce.repository.RoleRepository;
import rest.api.ezcommerce.repository.UserRepository;
import rest.api.ezcommerce.security.JwtUtil;
import rest.api.ezcommerce.security.SecurityConstants;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecurityConstants securityConstants;

    @Autowired
    private ObjectMapper objectMapper;

    private final String email = "test@gmail.com";
    private final String password = "rahasia";

    private final String title = "Home address";
    private final String address = "Jl Pasirluyu";
    private final String country = "Indonesia";
    private final String city = "Bandung";
    private final String postalCode = "40254";

    @BeforeEach
    void setUp() {                

        addressRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);
    }

    @Test
    void testRegisterAddressSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getTitle(), response.getData().getTitle());        
            assertEquals(request.getAddress(), response.getData().getAddress());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
        });
    }

    @Test
    void testRegisterAddressDuplicate() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterAddressBlank() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle("");
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterAddressInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterAddressTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterAddressNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                           
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterAddressBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        RegisterAddressRequest request = new RegisterAddressRequest();
        request.setTitle(title);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPostalCode(postalCode);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(addr.getTitle(), response.getData().getTitle());
            assertEquals(addr.getAddress(), response.getData().getAddress());
            assertEquals(addr.getCountry(), response.getData().getCountry());
            assertEquals(addr.getCity(), response.getData().getCity());
            assertEquals(addr.getPostalCode(), response.getData().getPostalCode());
        });
    }

    @Test
    void testGetAddressInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressBadId() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/" + addr.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetAddressNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/999999")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListAddressSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testGetListAddressInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/addresses/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetListAddressTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetListAddressNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/addresses/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                      
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetListAddressBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetAddressesSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testGetAddressesInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetAddressesTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetAddressesNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                     
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetAddressesBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getTitle(), response.getData().getTitle());        
            assertEquals(request.getAddress(), response.getData().getAddress());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
        });
    }

    @Test
    void testUpdateAddressDuplicate() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        AddressEntity addrOffice = new AddressEntity();
        addrOffice.setTitle("Office Address");
        addrOffice.setAddress(address);
        addrOffice.setCountry(country);
        addrOffice.setCity(city);
        addrOffice.setPostalCode(postalCode);
        addrOffice.setUserEntity(user);
        addressRepository.save(addrOffice);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle("Office Address");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressBadId() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/999999")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                             
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateAddressBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setTitle(title + "updated");
        request.setAddress(address + "updated");
        request.setCountry(country + "updated");
        request.setCity(city + "updated");
        request.setPostalCode(postalCode + "updated");

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteAddressesSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesBadId() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/addresses/999999")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - securityConstants.getJwtExpiration());
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteAddressesBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + securityConstants.getJwtExpiration());
        userRepository.save(user);        

        mockMvc.perform(
                delete("/api/addresses/" + addr.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }
}
