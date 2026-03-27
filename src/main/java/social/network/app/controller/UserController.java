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
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("registering userRegisterRequest: {}", userRegisterRequest);
        UserRegisterResponse registerResponse = userService.register(userRegisterRequest);
        log.info("register successful: {}", registerResponse);
        return ResponseEntity.ok(registerResponse);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<UserInfo> getById(@PathVariable UUID id) {
        UserInfo userInfo = userService.getById(id);
        log.info("getById successful: {}", userInfo);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<UserInfo>> search(@RequestParam("first_name") String firstName, @RequestParam("last_name") String lastName) {
        List<UserInfo> userInfoList = userService.search(firstName, lastName);
        log.info("search successful: {}", userInfoList);
        return ResponseEntity.ok(userInfoList);
    }

}
