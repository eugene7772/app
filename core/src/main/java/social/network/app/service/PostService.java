package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.entity.Post;
import social.network.app.mapper.PostMapper;
import social.network.app.repository.PostRepository;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    public UUID create(PostCreateRequest postCreateRequest, OffsetDateTime createdAt) {
        return postRepository.save(postMapper.toEntity(postCreateRequest, createdAt));
    }

    public void update(PostUpdateRequest postUpdateRequest) {
        postRepository.update(postMapper.toEntity(postUpdateRequest));
    }

    public void markDeleted(UUID id) {
        postRepository.markDeleted(id);
    }

    public void markDeleting(UUID id) {
        postRepository.markDeleting(id);
    }

    public void markDeleteFailed(UUID id) {
        postRepository.markDeleteFailed(id);
    }

    public void markActive(UUID id) {
        postRepository.markActive(id);
    }

    public void markCreationFailed(UUID id) {
        postRepository.markCreationFailed(id);
    }

    public Post get(UUID id) {
        return postRepository.get(id).orElse(null);
    }

    public Post getForDelete(UUID id) {
        return postRepository.getForDelete(id).orElse(null);
    }

    public List<Post> getAllLastPostsByUsers(List<UUID> allIds) {
        return postRepository.getAllLastPostsByUsers(allIds);
    }

    public List<Post> findAll(List<UUID> postIds) {
        return postRepository.findAll(postIds);
    }

}
