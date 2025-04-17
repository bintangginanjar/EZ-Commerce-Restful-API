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

import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.RoleEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.model.ProductResponse;
import rest.api.ezcommerce.model.RegisterProductRequest;
import rest.api.ezcommerce.model.UpdateProductRequest;
import rest.api.ezcommerce.model.WebResponse;
import rest.api.ezcommerce.repository.CategoryRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.RoleRepository;
import rest.api.ezcommerce.repository.UserRepository;
import rest.api.ezcommerce.security.JwtUtil;
import rest.api.ezcommerce.security.SecurityConstants;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

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
    //private final String categoryGoods = "Goods";
    //private final String categorySouvenir = "Souvenirs";

    private final String productName = "DJI Drone";
    private final String productDescription = "DJI Drone 2K24";
    private final Double productPrice = 25.0;
    private final Integer productStock = 10;

    @BeforeEach
    void setUp() {                

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
    void testRegisterProductSuccess() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getDescription(), response.getData().getDescription());
            assertEquals(request.getPrice(), response.getData().getPrice());
            assertEquals(request.getStock(), response.getData().getStock());
        });
    }

    @Test
    void testRegisterProductBlank() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName("");
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProductBadCategory() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "a/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProductCategoryNotFound() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/999999/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProductInvalidToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProductTokenExpired() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterProductNoToken() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                              
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testRegisterBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_NOT_FOUND").orElse(null);
        
        user.setRoles(Collections.singletonList(role));          
        userRepository.save(user);

        CategoryEntity category = new CategoryEntity();
        category.setName(categoryToys);
        category.setUserEntity(user);
        categoryRepository.save(category);

        RegisterProductRequest request = new RegisterProductRequest();
        request.setName(productName);
        request.setDescription(productDescription);
        request.setPrice(productPrice);
        request.setStock(productStock);

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
                post("/api/categories/" + category.getId() + "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductSuccess() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(product.getName(), response.getData().getName());
            assertEquals(product.getDescription(), response.getData().getDescription());
            assertEquals(product.getPrice(), response.getData().getPrice());
            assertEquals(product.getStock(), response.getData().getStock());
        });
    }

    @Test
    void testGetProductBadCategory() throws Exception {
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
                get("/api/categories/" + category.getId() + "a/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductCategoryNotFound() throws Exception {
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
                get("/api/categories/111111/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductBadProduct() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/" + product.getId() + "a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductNotFound() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/111111")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductInvalidToken() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductTokenExpired() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductNoToken() throws Exception {
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

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                  
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductBadRole() throws Exception {
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
                get("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListProductSuccess() throws Exception {
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
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetListProductInvalidToken() throws Exception {
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
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListProductTokenExpired() throws Exception {
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
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListProductNoToken() throws Exception {
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

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() - SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                      
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetListProductBadRole() throws Exception {
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
                get("/api/products/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductsSuccess() throws Exception {
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
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetProductsInvalidToken() throws Exception {
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
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductsTokenExpired() throws Exception {
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
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductsNoToken() throws Exception {
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

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);        

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                              
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetProductsBadRole() throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);
        
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
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", mockBearerToken)                                              
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateProductSuccess() throws Exception {
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

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName(productName + " updated");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

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
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getDescription(), response.getData().getDescription());
            assertEquals(request.getPrice(), response.getData().getPrice());
            assertEquals(request.getStock(), response.getData().getStock());
        });
    }

    @Test
    void testUpdateProductDuplicate() throws Exception {
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

        ProductEntity toys = new ProductEntity();
        toys.setName("Unicycle");
        toys.setDescription("Unicycle");
        toys.setPrice(productPrice);
        toys.setStock(productStock);
        toys.setCategoryEntity(category);
        toys.setUserEntity(user);
        productRepository.save(toys);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Unicycle");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

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
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());        
        });
    }

    @Test
    void testUpdateProductInvalidToken() throws Exception {
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

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName(productName + " updated");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

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
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProductTokenExpired() throws Exception {
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

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName(productName + " updated");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

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
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProductNoToken() throws Exception {
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

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName(productName + " updated");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                               
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateProductBadRole() throws Exception {
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

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName(productName + " updated");
        request.setDescription(productDescription + " updated");
        request.setPrice(productPrice + 5);
        request.setStock(productStock + 10);

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
                patch("/api/categories/" + category.getId() + "/products/" + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                        
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testDeleteProductSuccess() throws Exception {
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
                delete("/api/categories/" + category.getId() + "/products/" + product.getId())
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
    void testDeleteProductInvalidToken() throws Exception {
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
                delete("/api/categories/" + category.getId() + "/products/" + product.getId())
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
    void testDeleteProductTokenExpired() throws Exception {
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
                delete("/api/categories/" + category.getId() + "/products/" + product.getId())
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
    void testDeleteProductNoToken() throws Exception {
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

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                email, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);

        user.setToken(mockToken);
        user.setTokenExpiredAt(System.currentTimeMillis() + SecurityConstants.JWTexpiration);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/categories/" + category.getId() + "/products/" + product.getId())
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
    void testDeleteProductBadRole() throws Exception {
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
                delete("/api/categories/" + category.getId() + "/products/" + product.getId())
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
