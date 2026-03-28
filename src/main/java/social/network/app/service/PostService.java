package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.entity.Post;
import social.network.app.mapper.PostMapper;
import social.network.app.repository.PostRepository;

import java.util.*;

@Service
@Slf4j
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    public UUID create(PostCreateRequest postCreateRequest) {
        return postRepository.save(postMapper.toEntity(postCreateRequest));
    }

    public void update(PostUpdateRequest postUpdateRequest) {
        postRepository.update(postMapper.toEntity(postUpdateRequest));
    }

    public void delete(UUID id) {
        postRepository.delete(id);
    }

    public Post get(UUID id) {
        return postRepository.get(id).orElse(null);
    }

    public List<Post> getAllLastPostsByUsers(List<UUID> allIds) {
        return postRepository.getAllLastPostsByUsers(allIds);
    }

    public List<Post> findAll(List<UUID> postIds) {
        return postRepository.findAll(postIds);
    }

}
