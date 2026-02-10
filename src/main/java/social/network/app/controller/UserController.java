package social.network.app.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.network.app.dto.UserLoginRequest;
import social.network.app.dto.UserLoginResponse;
import social.network.app.dto.UserRegisterRequest;
import social.network.app.dto.UserRegisterResponse;
import social.network.app.entity.UserInfo;
import social.network.app.service.UserService;

import java.util.List;
import java.util.UUID;


@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse loginResponse = userService.login(userLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(value = "/user/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserRegisterResponse registerResponse = userService.register(userRegisterRequest);
        return ResponseEntity.ok(registerResponse);
    }

    @GetMapping(value = "/user/get/{id}")
    public ResponseEntity<UserInfo> getById(@PathVariable UUID id) {
        UserInfo userInfo = userService.getById(id);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping(value = "/user/search")
    public ResponseEntity<List<UserInfo>> search(@RequestParam("first_name") String firstName, @RequestParam("last_name") String lastName) {
        List<UserInfo> userInfoList = userService.search(firstName, lastName);
        return ResponseEntity.ok(userInfoList);
    }

}
