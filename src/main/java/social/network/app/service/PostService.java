package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.dto.PostCreateRequest;
import social.network.app.mapper.PostMapper;
import social.network.app.repository.PostRepository;

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

}
