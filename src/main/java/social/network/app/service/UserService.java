package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.dto.UserLoginRequest;
import social.network.app.dto.UserLoginResponse;
import social.network.app.dto.UserRegisterRequest;
import social.network.app.dto.UserRegisterResponse;
import social.network.app.entity.User;
import social.network.app.entity.UserEntity;
import social.network.app.entity.UserInfo;
import social.network.app.mapper.UserMapper;
import social.network.app.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static social.network.app.constants.ErrorConstants.PASSWORD_INCORRECT;
import static social.network.app.constants.ErrorConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            String passwordHash = passwordEncoder.encode(userRegisterRequest.getPassword());
            UserEntity userEntity = userMapper.toEntity(userRegisterRequest, passwordHash);
            UUID id = userRepository.save(userEntity);
            return new UserRegisterResponse(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        try {
            Optional<User> userOptional = userRepository.getBaseById(userLoginRequest.getId());
            if (userOptional.isEmpty()) {
                throw new Exception(USER_NOT_FOUND);
            }
            User user = userOptional.get();
            if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPasswordHash())) {
                throw new Exception(PASSWORD_INCORRECT);
            }
            return new UserLoginResponse(UUID.randomUUID().toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Transactional
    public UserInfo getById(UUID user_id) {
        try {
            Optional<UserInfo> userInfoOptional = userRepository.getFullById(user_id);
            if (userInfoOptional.isEmpty()) {
                throw new Exception(USER_NOT_FOUND);
            }
            return userInfoOptional.get();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
