package social.network.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostResponse;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.service.application.PostApplicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostApplicationService postApplicationService;

    @PostMapping(value = "/create")
    public ResponseEntity<String> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PostCreateRequest postCreateRequest) {
        String postId = postApplicationService.create(UUID.fromString(jwt.getSubject()), postCreateRequest).toString();
        return ResponseEntity.ok(postId);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<String> update(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        postApplicationService.update(postUpdateRequest);
        return ResponseEntity.ok("Post updated");
    }

    @PutMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") UUID id) {
        postApplicationService.delete(UUID.fromString(jwt.getSubject()), id);
        return ResponseEntity.ok("Post deleted");
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<String> get(@PathVariable("id") UUID id) {
        PostResponse postResponse = postApplicationService.get(id);
        return ResponseEntity.ok(postResponse.toString());
    }

    @GetMapping(value = "/feed")
    public ResponseEntity<List<PostResponse>> feed(@AuthenticationPrincipal Jwt jwt, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        List<PostResponse> posts = postApplicationService.feed(UUID.fromString(jwt.getSubject()), offset, limit);
        return ResponseEntity.ok(posts);
    }

}
