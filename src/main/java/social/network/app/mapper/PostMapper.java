package social.network.app.mapper;

import org.springframework.stereotype.Service;
import social.network.app.dto.PostCreateRequest;
import social.network.app.entity.Post;

@Service
public class PostMapper {
    public Post toEntity(PostCreateRequest postCreateRequest) {
        Post post = new Post();
        post.setText(postCreateRequest.getText());
        post.setAuthorUserId(postCreateRequest.getAuthorUserId());
        return post;
    }
}
