package rest.api.ezcommerce.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.CategoryEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.CategoryResponse;
import rest.api.ezcommerce.model.RegisterCategoryRequest;
import rest.api.ezcommerce.model.UpdateCategoryRequest;
import rest.api.ezcommerce.repository.CategoryRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class CategoryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ValidationService validationService;

    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.validationService = validationService;
    }

    @Transactional
    public CategoryResponse register(Authentication authentication, RegisterCategoryRequest request) {
        validationService.validate(request);

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already registered");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = new CategoryEntity();
        category.setName(request.getName());
        category.setUserEntity(user);
        categoryRepository.save(category);

        return ResponseMapper.ToCategoryResponseMapper(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Authentication authentication, String strCategoryId) {
        Integer categoryId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return ResponseMapper.ToCategoryResponseMapper(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CategoryEntity> categories = categoryRepository.findAllByUserEntity(user);

        return ResponseMapper.ToCategoryResponseListMapper(categories);
    }

    @Transactional
    public CategoryResponse update(Authentication authentication, UpdateCategoryRequest request, String strCategoryId) {
        Integer categoryId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (Objects.nonNull(request.getName())) {
            category.setName(request.getName());
        }

        categoryRepository.save(category);

        return ResponseMapper.ToCategoryResponseMapper(category);
    }

    @Transactional
    public void delete(Authentication authentication, String strCategoryId) {
        Integer categoryId = 0;

        try {
            categoryId = Integer.parseInt(strCategoryId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity category = categoryRepository.findFirstByUserEntityAndId(user, categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete category failed");
        } 
    }

}
