package social.network.app.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import social.network.app.dto.UserLoginRequest;
import social.network.app.dto.UserLoginResponse;
import social.network.app.service.UserService;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse loginResponse = userService.login(userLoginRequest);
        log.info("login successful: {}", loginResponse);
        return ResponseEntity.ok(loginResponse);
    }

}
