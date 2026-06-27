package social.network.app.service.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.dto.*;
import social.network.app.entity.Post;
import social.network.app.entity.PostStatus;
import social.network.app.exception.PostCreationException;
import social.network.app.exception.PostDeleteException;
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

    @Transactional(noRollbackFor = PostCreationException.class)
    public UUID create(UUID userId, PostCreateRequest postCreateRequest) {
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID postId = postService.create(postCreateRequest, createdAt);
        log.info("Post {} saved with PUBLISHING status.", postId);

        List<UUID> friends = friendService.getAllById(userId);
        PostDto post = PostDto.builder()
                .postId(postId.toString())
                .postText(postCreateRequest.getText())
                .authorUserId(postCreateRequest.getAuthorUserId().toString())
                .createdAt(createdAt)
                .status(PostStatus.ACTIVE)
                .eventType("post_created")
                .build();
        try {
            kafkaService.sendPostAndWait(post);
            postService.markActive(postId);
            log.info("Post {} activated after kafka event was sent.", postId);
        } catch (Exception e) {
            log.error(SEND_KAFKA_ERROR, postId, e);
            postService.markCreationFailed(postId);
            throw new PostCreationException(POST_CREATE_ERROR, e);
        }
        try {
            friends.forEach(friendId -> {
                feedWebSocketPushService.sendPostToUser(friendId.toString(), post);
            });
        } catch (Exception e) {
            log.error(SEND_WS_ERROR, postId, e);
        }
        try {
            postCacheService.addPostToManyFeeds(friends, postId);
            postCacheService.addPostToUserFeed(userId, postId);
            log.info("Post {} added in cache to: {}", postId, friends);
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

    @Transactional(noRollbackFor = PostDeleteException.class)
    public void delete(UUID userId, UUID id) {
        Post post = postService.getForDelete(id);
        if (post == null) {
            log.warn("Post {} not found or already deleted.", id);
            return;
        }
        if (userId.equals(post.getAuthorUserId())) {
            postService.markDeleting(id);
        } else {
            log.warn("This user: {}, don't created this post: {}", userId, id);
            return;
        }
        log.info("Post {} moved to DELETING status.", id);
        List<UUID> friends = Collections.emptyList();
        try {
            postCacheService.removePostFromUserFeed(userId, id);
            friends = friendService.getAllById(userId);
            postCacheService.removePostFromManyFeeds(friends, id);
            log.info("Post {} removed from cache for: {}", id, userId);
        } catch (Exception e) {
            log.error(CACHE_ERROR, id, e);
        }
        PostDto deletedPost = PostDto.builder()
                .postId(id.toString())
                .postText(post.getText())
                .authorUserId(post.getAuthorUserId().toString())
                .createdAt(post.getCreatedAt())
                .status(PostStatus.DELETED)
                .eventType("post_deleted")
                .build();
        try {
            kafkaService.sendPostDeletedAndWait(deletedPost);
            postService.markDeleted(id);
            log.info("Post {} moved to DELETED status after kafka delete event was sent.", id);
        } catch (Exception e) {
            log.error(SEND_KAFKA_ERROR, id, e);
            postService.markDeleteFailed(id);
            throw new PostDeleteException(POST_DELETE_ERROR, e);
        }
        try {
            List<UUID> recipients = new ArrayList<>(friends);
            recipients.add(userId);
            recipients.forEach(recipientId ->
                    feedWebSocketPushService.sendPostToUser(recipientId.toString(), deletedPost));
        } catch (Exception e) {
            log.error(SEND_WS_ERROR, id, e);
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
