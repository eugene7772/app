package social.network.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.network.app.entity.Friend;
import social.network.app.repository.FriendRepository;

import java.util.UUID;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public void set(UUID userId, UUID friendId) {
        friendRepository.save(new Friend(userId, friendId));
    }

    public void delete(UUID userId, UUID friendId) {
        friendRepository.delete(new Friend(userId, friendId));
    }

}
