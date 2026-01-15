package social.network.app.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import social.network.app.dto.UserRegisterRequest;


@RestController
@Slf4j
public class UserController {

    @RequestMapping(value = "/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Ok");
    }

    @RequestMapping(value = "/user/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return ResponseEntity.ok("Ok");
    }

    @RequestMapping(value = "/user/get/{id}")
    public ResponseEntity<String> getById(@PathVariable String id) {
        return ResponseEntity.ok("Ok");
    }

}
