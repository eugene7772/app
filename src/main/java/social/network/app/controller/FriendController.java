package social.network.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friend")
@Slf4j
public class FriendController {

    @PutMapping(value = "/set/{user_id}")
    public ResponseEntity<String> set(@PathVariable("user_id") String userId) {
        log.info(userId);
        return ResponseEntity.ok("AA");
    }

    @PutMapping(value = "/delete/{user_id}")
    public ResponseEntity<String> delete(@PathVariable("user_id") String userId) {
        log.info(userId);
        return ResponseEntity.ok("AA");
    }

}
