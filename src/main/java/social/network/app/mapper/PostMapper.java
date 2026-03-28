package social.network.app.mapper;

import org.springframework.stereotype.Service;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostResponse;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.entity.Post;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PostMapper {
    public Post toEntity(PostCreateRequest postCreateRequest) {
        Post post = new Post();
        post.setText(postCreateRequest.getText());
        post.setAuthorUserId(postCreateRequest.getAuthorUserId());
        post.setCreatedAt(OffsetDateTime.now());
        return post;
    }

    public Post toEntity(PostUpdateRequest postUpdateRequest) {
        Post post = new Post();
        post.setId(postUpdateRequest.getId());
        post.setText(postUpdateRequest.getText());
        return post;
    }

    public List<PostResponse> toPostResponseList(List<Post> posts) {
        return posts.stream().map(PostResponse::new).toList();
    }

}
