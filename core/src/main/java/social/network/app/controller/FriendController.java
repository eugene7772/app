package social.network.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import social.network.app.service.application.FriendApplicationService;

import java.util.UUID;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendApplicationService friendApplicationService;

    @PutMapping(value = "/set/{user_id}")
    public ResponseEntity<String> set(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") String friendUserId) {
        friendApplicationService.follow(UUID.fromString(jwt.getSubject()), UUID.fromString(friendUserId));
        return ResponseEntity.ok("Friend added");
    }

    @PutMapping(value = "/delete/{user_id}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") String friendUserId) {
        friendApplicationService.unFollow(UUID.fromString(jwt.getSubject()), UUID.fromString(friendUserId));
        return ResponseEntity.ok("Friend deleted");
    }

}
