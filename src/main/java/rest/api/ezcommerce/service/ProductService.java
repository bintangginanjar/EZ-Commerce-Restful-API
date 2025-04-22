package rest.api.ezcommerce.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.ProductEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.ProductResponse;
import rest.api.ezcommerce.model.RegisterProductRequest;
import rest.api.ezcommerce.model.UpdateProductRequest;
import rest.api.ezcommerce.repository.CategoryRepository;
import rest.api.ezcommerce.repository.ProductRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ValidationService validationService;

    public ProductService(UserRepository userRepository, CategoryRepository categoryRepository,
            ProductRepository productRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ProductResponse register(Authentication authentication, RegisterProductRequest request, String strCategoryId) {
        Integer categoryId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        validationService.validate(request);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (productRepository.findByUserEntityAndName(user, request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product already registered");
        }

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryEntity(category);
        product.setUserEntity(user);
        productRepository.save(product);

        return ResponseMapper.ToProductResponseMapper(product);

    }

    @Transactional(readOnly = true)
    public ProductResponse get(Authentication authentication, String strCategoryId, String strProductId) {
        Integer categoryId = 0;
        Integer productId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);
            productId = Integer.parseInt(strProductId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        ProductEntity product = productRepository.findFirstByCategoryEntityAndId(category, productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return ResponseMapper.ToProductResponseMapper(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ProductEntity> products = productRepository.findAllByUserEntity(user);

        return ResponseMapper.ToProductResponseListMapper(products);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listAll() {
        List<ProductEntity> products = productRepository.findAll();

        return ResponseMapper.ToProductResponseListMapper(products);
    }

    @Transactional(readOnly = true)
    public ProductResponse update(Authentication authentication, UpdateProductRequest request, String strCategoryId, String strProductId) {
        Integer categoryId = 0;
        Integer productId = 0;        

        try {
            categoryId = Integer.parseInt(strCategoryId);
            productId = Integer.parseInt(strProductId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));        

        ProductEntity product = productRepository.findFirstByCategoryEntityAndId(category, productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));        

        List<ProductEntity> products = productRepository.findAllByUserEntityAndAndName(user, request.getName());
        log.info("PRODUCT SIZE : ", products.size());
        if (products.size() >= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product already registered");
        }
        
        if (Objects.nonNull(request.getName())) {
            product.setName(request.getName());
        }
        
        if (Objects.nonNull(request.getDescription())) {
            product.setDescription(request.getDescription());
        }

        if (Objects.nonNull(request.getPrice())) {
            product.setPrice(request.getPrice());
        }

        if (Objects.nonNull(request.getStock())) {
            product.setStock(request.getStock());
        }

        return ResponseMapper.ToProductResponseMapper(product);
    }

    @Transactional
    public void delete(Authentication authentication, String strCategoryId, String strProductId) {
        Integer categoryId = 0;
        Integer productId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);
            productId = Integer.parseInt(strProductId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        ProductEntity product = productRepository.findFirstByCategoryEntityAndId(category, productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        try {
            productRepository.delete(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete product failed");
        }                    
    }

}
