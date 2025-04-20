package rest.api.ezcommerce.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
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
import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.OrderEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.RoleEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.OrderItemResponse;
import rest.api.ezcommerce.model.RegisterOrderItemRequest;
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
public class OrderItemControllerTest {

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

    private final String categoryToys = "Toys";

    private final String productName = "DJI Drone";
    private final String productDescription = "DJI Drone 2K24";
    private final Double productPrice = 25.0;
    private final Integer productStock = 10;

    private final Integer productQuantity = 2;
    private final Double productAmount = productPrice * productQuantity;

    private final String orderStatus = "Waiting payment";
    private final String orderRemark = "Handle with care";

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
    void testRegisterOrderItemSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(order.getOrderId(), response.getData().getOrderId());
            assertEquals(product.getId(), response.getData().getProductId());
            assertEquals(product.getName(), response.getData().getProductName());
            assertEquals(product.getPrice(), response.getData().getProductPrice());
            assertEquals(request.getQuantity(), response.getData().getQuantity());
            assertEquals(request.getAmount(), response.getData().getAmount());

        });
    }

    @Test
    void testRegisterOrderItemBlank() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(null);
        request.setQuantity(null);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());

        });
    }

    @Test
    void testRegisterOrderItemOrderNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "-1a1a" + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());

        });
    }

    @Test
    void testRegisterOrderItemInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderItemTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderItemNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                           
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterOrderItemBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(productPrice);
        product.setStock(productStock);
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

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
        order.setTotalAmount(productAmount);
        order.setStatus(orderStatus);
        order.setRemark(orderRemark);
        order.setUserEntity(user);
        order.setAddressEntity(addr);
        orderRepository.save(order);

        RegisterOrderItemRequest request = new RegisterOrderItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);
        request.setAmount(productAmount);
        
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
                post("/api/orders/" + order.getOrderId() + "/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<OrderItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }
}
