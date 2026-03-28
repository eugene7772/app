package social.network.app.service.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.app.service.FriendService;
import social.network.app.service.cache.PostCacheService;

import java.util.List;
import java.util.UUID;

import static social.network.app.constants.ErrorConstants.CACHE_FRIEND_ERROR;

@Service
@Slf4j
public class FriendApplicationService {

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostCacheService postCacheService;

    @Transactional
    public void follow(UUID userId, UUID friendId) {
        List<UUID> friends = friendService.getAllById(userId);
        if (!friends.isEmpty()) {
            log.warn("That friend is exist");
            return;
        }
        friendService.set(userId, friendId);
        log.info("Set friend: {} for {}", friendId, userId);
        try {
            postCacheService.evictUserFeed(userId);
            log.info("Clear cache for: {} (new friend)", userId);
        } catch (Exception e) {
            log.error(CACHE_FRIEND_ERROR, userId, e);
        }
    }

    @Transactional
    public void unFollow(UUID userId, UUID friendId) {
        friendService.delete(userId, friendId);
        log.info("Unfollow friend: {} for {}", friendId, userId);
        try {
            postCacheService.evictUserFeed(userId);
            log.info("Clear cache for: {} (friend deleted)", userId);
        } catch (Exception e) {
            log.error(CACHE_FRIEND_ERROR, userId, e);
        }
    }

}
