package social.network.app.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.service.PostService;

import java.util.UUID;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/create")
    public ResponseEntity<String> create(@Valid @RequestBody PostCreateRequest postCreateRequest) {
        return ResponseEntity.ok(postService.create(postCreateRequest).toString());
    }

    @PutMapping(value = "/update")
    public ResponseEntity<String> update(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        log.info(postUpdateRequest.toString());
        return ResponseEntity.ok("update");
    }

    @PutMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") UUID id) {
        log.info(id.toString());
        return ResponseEntity.ok("delete");
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<String> get(@PathVariable("id") UUID id) {
        log.info(id.toString());
        return ResponseEntity.ok("get");
    }

    @GetMapping(value = "/feed")
    public ResponseEntity<String> feed(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        log.info(offset + " " + limit);
        return ResponseEntity.ok("feed");
    }

}
