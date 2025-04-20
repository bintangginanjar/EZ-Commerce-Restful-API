package rest.api.ezcommerce.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.RoleEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.OrderResponse;
import rest.api.ezcommerce.model.RegisterOrderRequest;
import rest.api.ezcommerce.model.UpdateOrderRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.repository.AddressRepository;
import rest.api.ezcommerce.repository.CategoryRepository;
import rest.api.ezcommerce.repository.OrderItemRepository;
import rest.api.ezcommerce.repository.OrderRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.RoleRepository;
import rest.api.ezcommerce.repository.UserRepository;
import rest.api.ezcommerce.security.JwtUtil;
import rest.api.ezcommerce.security.SecurityConstants;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String email = "test@gmail.com";
    private final String password = "rahasia";

    private final String orderStatus = "Waiting payment";
    private final String orderRemark = "Handle with care";
    private final Double orderAmount = 50.0;

    private final String title = "Home address";
    private final String address = "Jl Pasirluyu";
    private final String country = "Indonesia";
    private final String city = "Bandung";
    private final String postalCode = "40254";

    @BeforeEach
    void setUp() {                
        
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        addressRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));        
        userRepository.save(user);

    }

    @Test
    void testRegisterOrderSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(addr.getId());
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNotNull(response.getData().getId());
            assertFalse(response.getData().getOrderId().isBlank());
            assertEquals(request.getStatus(), response.getData().getStatus());
            assertEquals(request.getRemark(), response.getData().getRemark());
            assertEquals(request.getTotalAmount(), response.getData().getTotalAmount());
        });
    }

    @Test
    void testRegisterOrderBlank() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(null);
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(addr.getId());
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(addr.getId());
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(addr.getId());
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                          
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderBadRole() throws Exception {
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

        RegisterOrderRequest request = new RegisterOrderRequest();
        request.setAddressId(addr.getId());
        request.setStatus(orderStatus);
        request.setRemark(orderRemark);
        request.setTotalAmount(orderAmount);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(order.getOrderId(), response.getData().getOrderId());
            assertEquals(order.getStatus(), response.getData().getStatus());
            assertEquals(order.getRemark(), response.getData().getRemark());
            assertEquals(order.getTotalAmount(), response.getData().getTotalAmount());
        });
    }

    @Test
    void testGetOrderNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                     
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderBadRole() throws Exception {
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

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderListSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<OrderResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetOrderListInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<OrderResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderListTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<OrderResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderListNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                     
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<OrderResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetOrderListBadRole() throws Exception {
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

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<OrderResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateOrderSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
            assertEquals(request.getStatus(), response.getData().getStatus());
            assertEquals(request.getRemark(), response.getData().getRemark());
            assertEquals(request.getTotalAmount(), response.getData().getTotalAmount());
        });
    }

    @Test
    void testUpdateOrderInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateOrderTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateOrderNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId() + "-aaaa")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateOrderNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        AddressEntity addr = new AddressEntity();
        addr.setTitle(title);
        addr.setAddress(address);
        addr.setCountry(country);
        addr.setCity(city);
        addr.setPostalCode(postalCode);
        addr.setUserEntity(user);
        addressRepository.save(addr);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                          
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateOrderBadRole() throws Exception {
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

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotalAmount(orderAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(orderStatus + " updated");
        request.setRemark(orderRemark + " updated");
        request.setTotalAmount(orderAmount + 10);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/orders/" + order.getOrderId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<OrderResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }
}
