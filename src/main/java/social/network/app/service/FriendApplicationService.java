package social.network.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FriendApplicationService {

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostCacheService postCacheService;

    @Transactional
    public void follow(UUID userId, UUID friendId) {
        friendService.set(userId, friendId);
        postCacheService.evictUserFeed(userId);
    }

    @Transactional
    public void unFollow(UUID userId, UUID friendId) {
        friendService.delete(userId, friendId);
        postCacheService.evictUserFeed(userId);
    }

}
