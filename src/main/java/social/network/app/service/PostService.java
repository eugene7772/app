package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.dto.PostCreateRequest;
import social.network.app.dto.PostResponse;
import social.network.app.dto.PostUpdateRequest;
import social.network.app.mapper.PostMapper;
import social.network.app.repository.PostRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Transactional
    public UUID create(PostCreateRequest postCreateRequest) {
        return postRepository.save(postMapper.toEntity(postCreateRequest));
    }

    @Transactional
    public void update(PostUpdateRequest postUpdateRequest) {
    }

    @Transactional
    public void delete(UUID id) {
    }

    @Transactional
    public PostResponse get(UUID id) {
        return null;
    }

    @Transactional
    @Cacheable(value = "feed", key = "#offset + '_' + #limit")
    public List<PostResponse> feed(Integer offset, Integer limit) {
        return null;
    }

}
