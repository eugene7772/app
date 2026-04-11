package social.network.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.auth.dto.UserLoginRequest;
import social.network.auth.dto.UserLoginResponse;
import social.network.auth.entity.User;
import social.network.auth.exception.PasswordIncorrectException;
import social.network.auth.exception.UserNotFoundException;
import social.network.auth.repository.UserRepository;
import social.network.auth.service.jwt.JwtService;

@Service
public class LoginService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.getBaseById(userLoginRequest.getId()).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPasswordHash())) {
            throw new PasswordIncorrectException();
        }
        return new UserLoginResponse(jwtService.generateToken(String.valueOf(userLoginRequest.getId())));
    }
}
