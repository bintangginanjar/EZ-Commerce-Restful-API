package rest.api.ezcommerce.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.ProfileEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.ProfileResponse;
import rest.api.ezcommerce.model.RegisterProfileRequest;
import rest.api.ezcommerce.model.UpdateProfileRequest;
import rest.api.ezcommerce.repository.ProfileRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ValidationService validationService;

    public ProfileService(UserRepository userRepository, ProfileRepository profileRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ProfileResponse register(Authentication authentication, RegisterProfileRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ProfileEntity profile = new ProfileEntity();
        profile.setUserEntity(user);
        profile.setFirstname(request.getFirstname());
        profile.setLastname(request.getLastname());        
        profile.setPhoneNumber(request.getPhoneNumber());                        
        profileRepository.save(profile);

        return ResponseMapper.ToProfileResponseMapper(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(Authentication authentication) {

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        ProfileEntity profile = profileRepository.findFirstByUserEntity(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        return ResponseMapper.ToProfileResponseMapper(profile);
    }

    @Transactional
    public ProfileResponse update(Authentication authentication, UpdateProfileRequest request) {

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        ProfileEntity profile = profileRepository.findFirstByUserEntity(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        if (Objects.nonNull(request.getFirstname())) {
            profile.setFirstname(request.getFirstname());
        }

        if (Objects.nonNull(request.getLastname())) {
            profile.setLastname(request.getLastname());
        }        

        if (Objects.nonNull(request.getPhoneNumber())) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }

        return ResponseMapper.ToProfileResponseMapper(profile);
    }

}
