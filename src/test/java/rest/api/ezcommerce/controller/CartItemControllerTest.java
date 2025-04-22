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

import rest.api.ezcommerce.entity.CartEntity;
import rest.api.ezcommerce.entity.CartItemEntity;
import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.RoleEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.CartItemResponse;
import rest.api.ezcommerce.model.RegisterCartItemRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.repository.CartItemRepository;
import rest.api.ezcommerce.repository.CartRepository;
import rest.api.ezcommerce.repository.CategoryRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.RoleRepository;
import rest.api.ezcommerce.repository.UserRepository;
import rest.api.ezcommerce.security.JwtUtil;
import rest.api.ezcommerce.security.SecurityConstants;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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
    private final Integer productQuantity = 5;

    @BeforeEach
    void setUp() {                
        
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();  
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
    void testRegisterCartItemSuccess() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);        
        
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
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(cart.getId(), response.getData().getCartId());            
            assertEquals(product.getName(), response.getData().getProductName());
            assertEquals(request.getQuantity(), response.getData().getQuantity());            
        });
    }

    @Test
    void testRegisterCartItemBlank() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(null);
        request.setQuantity(null);        
        
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
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());           
        });
    }

    @Test
    void testRegisterCartItemInvalidToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);        
        
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
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());          
        });
    }

    @Test
    void testRegisterCartItemTokenExpired() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);        
        
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
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());          
        });
    }

    @Test
    void testRegisterCartItemNoToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);        
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                             
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());           
        });
    }

    @Test
    void testRegisterCartItemBadRole() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        RegisterCartItemRequest request = new RegisterCartItemRequest();
        request.setIdProduct(product.getId());
        request.setQuantity(productQuantity);        
        
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
                post("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<CartItemResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetCartItemListSuccess() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                get("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<CartItemResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testGetCartItemListInvalidToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                get("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<CartItemResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetCartItemListTokenExpired() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                get("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<CartItemResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetCartItemListNoToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                  
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<CartItemResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetCartItemListBadRole() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                get("/api/carts/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<CartItemResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteCartItemSuccess() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/" + item.getId())
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
    void testDeleteCartItemNotFound() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/999999")
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
    void testDeleteCartItemBadId() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/" + item.getId() + "a")
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
    void testDeleteCartItemInvalidToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/" + item.getId())
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
    void testDeleteCartItemTokenExpired() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/" + item.getId())
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
    void testDeleteCartItemNoToken() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/carts/items/" + item.getId())
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
    void testDeleteCartItemBadRole() throws Exception {
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

        CartEntity cart = new CartEntity();
        cart.setTotalItems(productQuantity);
        cart.setUserEntity(user);
        cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.setProductEntity(product);
        item.setCartEntity(cart);
        item.setQuantity(productQuantity);
        cartItemRepository.save(item);       
        
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
                delete("/api/carts/items/" + item.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }
}
