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
import social.network.app.exception.PasswordIncorrectException;
import social.network.app.exception.UserNotFoundException;
import social.network.app.exception.UserRegisterException;
import social.network.app.mapper.UserMapper;
import social.network.app.repository.UserRepository;

import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            String passwordHash = passwordEncoder.encode(userRegisterRequest.getPassword());
            UserEntity userEntity = userMapper.toEntity(userRegisterRequest, passwordHash);
            UUID id = userRepository.save(userEntity);
            return new UserRegisterResponse(id);
        } catch (Exception e) {
            throw new UserRegisterException(e.getMessage());
        }
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.getBaseById(userLoginRequest.getId()).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPasswordHash())) {
            throw new PasswordIncorrectException();
        }
        return new UserLoginResponse(jwtService.generateToken(user.getLogin()));
    }

    @Transactional
    public UserInfo getById(UUID user_id) {
        return userRepository.getFullById(user_id).orElseThrow(UserNotFoundException::new);
    }

}
