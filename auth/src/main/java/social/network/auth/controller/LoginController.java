package social.network.auth.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import social.network.auth.dto.UserLoginRequest;
import social.network.auth.dto.UserLoginResponse;
import social.network.auth.service.LoginService;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse loginResponse = loginService.login(userLoginRequest);
        log.info("login successful: {}", loginResponse);
        return ResponseEntity.ok(loginResponse);
    }

}
