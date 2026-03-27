package social.network.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PostCacheService {

    private static final int MAX_FEED_SIZE = 1000;
    private static final Duration FEED_TTL = Duration.ofHours(6);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addPostToUserFeed(UUID userId, UUID postId) {
        String key = buildFeedKey(userId);
        redisTemplate.opsForList().leftPush(key, postId.toString());
        redisTemplate.opsForList().trim(key, 0, MAX_FEED_SIZE - 1);
        redisTemplate.expire(key, FEED_TTL);
    }

    public void addPostToManyFeeds(List<UUID> userIds, UUID postId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (UUID userId : userIds) {
            addPostToUserFeed(userId, postId);
        }
    }

    public List<UUID> getFeedPostIds(UUID userId, int offset, int limit) {
        if (limit <= 0 || offset < 0) {
            return Collections.emptyList();
        }

        String key = buildFeedKey(userId);

        List<String> values = redisTemplate.opsForList()
                .range(key, offset, offset + limit - 1);

        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(UUID::fromString)
                .toList();
    }

    public void evictUserFeed(UUID userId) {
        redisTemplate.delete(buildFeedKey(userId));
    }

    public void evictManyFeeds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        List<String> keys = userIds.stream()
                .map(this::buildFeedKey)
                .toList();

        redisTemplate.delete(keys);
    }

    public void removePostFromUserFeed(UUID userId, UUID postId) {
        String key = buildFeedKey(userId);
        redisTemplate.opsForList().remove(key, 0, postId.toString());
    }

    public void removePostFromManyFeeds(List<UUID> userIds, UUID postId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (UUID userId : userIds) {
            removePostFromUserFeed(userId, postId);
        }
    }

    public boolean hasFeedCache(UUID userId) {
        Boolean exists = redisTemplate.hasKey(buildFeedKey(userId));
        return Boolean.TRUE.equals(exists);
    }

    private String buildFeedKey(UUID userId) {
        return "feed:" + userId;
    }

}
