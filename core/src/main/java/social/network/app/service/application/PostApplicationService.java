package social.network.app.service.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.dto.*;
import social.network.app.entity.Post;
import social.network.app.mapper.PostMapper;
import social.network.app.service.FeedWebSocketPushService;
import social.network.app.service.FriendService;
import social.network.app.service.KafkaService;
import social.network.app.service.PostService;
import social.network.app.service.cache.PostCacheService;

import java.time.OffsetDateTime;
import java.util.*;

import static social.network.app.constants.ErrorConstants.*;

@Service
@Slf4j
public class PostApplicationService {
    @Autowired
    private PostCacheService postCacheService;

    @Autowired
    private PostService postService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FeedWebSocketPushService feedWebSocketPushService;

    @Autowired
    private KafkaService kafkaService;

    @Transactional
    public UUID create(UUID userId, PostCreateRequest postCreateRequest) {
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID postId = postService.create(postCreateRequest, createdAt);
        log.info("Post {} saved.", postId);

        List<UUID> friends = friendService.getAllById(userId);
        log.info("Post {} added in cache to: {}", postId, friends);
        PostDto post = PostDto.builder()
                .postId(postId.toString())
                .postText(postCreateRequest.getText())
                .authorUserId(postCreateRequest.getAuthorUserId().toString())
                .createdAt(createdAt)
                .build();
        try {
            friends.forEach(friendId -> {
                feedWebSocketPushService.sendPostToUser(friendId.toString(), post);
            });
        } catch (Exception e) {
            log.error(SEND_WS_ERROR, postId, e);
        }
        try {
            kafkaService.sendPost(post);
        } catch (Exception e) {
            log.error(SEND_KAFKA_ERROR, postId, e);
        }
        try {
            postCacheService.addPostToManyFeeds(friends, postId);
            postCacheService.addPostToUserFeed(userId, postId);
        } catch (Exception e) {
            log.error(CACHE_ERROR, postId, e);
        }
        return postId;
    }

    @Transactional
    public void update(PostUpdateRequest postUpdateRequest) {
        postService.update(postUpdateRequest);
        log.info("Post: {} updated.", postUpdateRequest);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        Post post = postService.get(id);
        if (userId.equals(post.getAuthorUserId())) {
            postService.delete(id);
        } else {
            log.warn("This user: {}, don't created this post: {}", userId, id);
            return;
        }
        log.info("Post {} deleted.", id);
        try {
            postCacheService.removePostFromUserFeed(userId, id);
            List<UUID> friends = friendService.getAllById(userId);
            postCacheService.removePostFromManyFeeds(friends, id);
            log.info("Post {} removed from cache for: {}", id, userId);
        } catch (Exception e) {
            log.error(CACHE_ERROR, id, e);
        }
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID id) {
        Post post = postService.get(id);
        log.info("Get post: {}", post);
        return PostResponse.builder()
                .id(post.getId())
                .text(post.getText())
                .authorUserId(post.getAuthorUserId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> feed(UUID userId, Integer offset, Integer limit) {
        if (offset < 0 || limit <= 0) {
            throw new IllegalArgumentException("Offset must be >= 0 and limit must be > 0");
        }
        List<UUID> postIds = postCacheService.getFeedPostIds(userId, offset, limit);
        log.info("Posts for: {}, in cache: {} ", userId, postIds);
        if (postIds.isEmpty()) {
            return rebuildFeed(userId, offset, limit);
        }
        List<Post> posts = postService.findAll(postIds);
        if (posts.size() != postIds.size()) {
            return rebuildFeed(userId, offset, limit);
        }
        Map<UUID, Integer> order = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            order.put(postIds.get(i), i);
        }
        posts.sort(Comparator.comparingInt(post -> order.getOrDefault(post.getId(), Integer.MAX_VALUE)));
        log.info("Feed posts from cache: {}", posts);
        return postMapper.toPostResponseList(posts);
    }

    @Transactional(readOnly = true)
    private List<PostResponse> rebuildFeed(UUID userId, int offset, int limit) {
        log.info("Post cache is empty or inconsistent, rebuilding...");
        List<UUID> allIds = new ArrayList<>(friendService.getAllById(userId));
        allIds.add(userId);
        List<Post> lastFriendsAndMyPosts = postService.getAllLastPostsByUsers(allIds);
        try {
            postCacheService.rebuildUserFeed(userId, lastFriendsAndMyPosts.stream().map(Post::getId).toList());
            log.info("Cache for user: {} rebuild", userId);
        } catch (Exception e) {
            log.error(CACHE_ERROR, userId, e);
        }
        log.info("Feed posts from db: {}", lastFriendsAndMyPosts);
        List<Post> lastFriendsAndMyPostsByLimit = lastFriendsAndMyPosts.stream()
                .skip(offset)
                .limit(limit)
                .toList();
        return postMapper.toPostResponseList(lastFriendsAndMyPostsByLimit);
    }

}
