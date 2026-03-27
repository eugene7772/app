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
import social.network.app.service.PostService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/create")
    public ResponseEntity<String> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PostCreateRequest postCreateRequest) {
        String postId = postService.create(postCreateRequest).toString();
        return ResponseEntity.ok(postId);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<String> update(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        postService.update(postUpdateRequest);
        return ResponseEntity.ok("Post updated");
    }

    @PutMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") UUID id) {
        postService.delete(id);
        return ResponseEntity.ok("Post deleted");
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<String> get(@PathVariable("id") UUID id) {
        PostResponse postResponse = postService.get(id);
        return ResponseEntity.ok(postResponse.toString());
    }

    @GetMapping(value = "/feed")
    public ResponseEntity<List<PostResponse>> feed(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        List<PostResponse> posts = postService.feed(offset, limit);
        return ResponseEntity.ok(posts);
    }

}
