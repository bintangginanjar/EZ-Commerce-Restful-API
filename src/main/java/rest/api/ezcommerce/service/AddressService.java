package rest.api.ezcommerce.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rest.api.ezcommerce.entity.AddressEntity;
import rest.api.ezcommerce.entity.UserEntity;
import rest.api.ezcommerce.mapper.ResponseMapper;
import rest.api.ezcommerce.model.AddressResponse;
import rest.api.ezcommerce.model.RegisterAddressRequest;
import rest.api.ezcommerce.model.UpdateAddressRequest;
import rest.api.ezcommerce.repository.AddressRepository;
import rest.api.ezcommerce.repository.UserRepository;

@Service
public class AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    public AddressService(UserRepository userRepository, AddressRepository addressRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.validationService = validationService;
    }

    @Transactional
    public AddressResponse register(Authentication authentication, RegisterAddressRequest request) {
        validationService.validate(request);

        if (addressRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address already registered");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AddressEntity address = new AddressEntity();
        address.setTitle(request.getTitle());
        address.setAddress(request.getAddress());
        address.setCountry(request.getCountry());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setUserEntity(user);
        addressRepository.save(address);

        return ResponseMapper.ToAddressResponseMapper(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse get(Authentication authentication, String strAddressId) {
        Integer addressId = 0;

        try {
            addressId = Integer.parseInt(strAddressId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AddressEntity address = addressRepository.findFirstByUserEntityAndId(user, addressId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return ResponseMapper.ToAddressResponseMapper(address);                        
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<AddressEntity> addresses = addressRepository.findAllByUserEntity(user);

        return ResponseMapper.ToAddressResponseListMapper(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> listAll() {        
        List<AddressEntity> addresses = addressRepository.findAll();

        return ResponseMapper.ToAddressResponseListMapper(addresses);
    }

    @Transactional
    public AddressResponse update(Authentication authentication, UpdateAddressRequest request, String strAddressId) {
        Integer addressId = 0;

        try {
            addressId = Integer.parseInt(strAddressId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));        

        AddressEntity address = addressRepository.findFirstByUserEntityAndId(user, addressId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (Objects.nonNull(request.getTitle())) {
            address.setTitle(request.getTitle());
        }

        if (Objects.nonNull(request.getAddress())) {
            address.setAddress(request.getAddress());
        }

        if (Objects.nonNull(request.getCountry())) {
            address.setCountry(request.getCountry());
        }

        if (Objects.nonNull(request.getCity())) {
            address.setCity(request.getCity());
        }

        if (Objects.nonNull(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
        }

        addressRepository.save(address);

        return ResponseMapper.ToAddressResponseMapper(address);
    }

    @Transactional
    public void delete(Authentication authentication, String strAddressId) {
        Integer addressId = 0;

        try {
            addressId = Integer.parseInt(strAddressId);       
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByEmail(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AddressEntity address = addressRepository.findFirstByUserEntityAndId(user, addressId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        try {
            addressRepository.delete(address);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete address failed");
        } 
    }


}
