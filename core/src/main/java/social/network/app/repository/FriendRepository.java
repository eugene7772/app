package social.network.app.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import social.network.app.entity.Friend;

import java.util.List;
import java.util.UUID;

@Repository
public class FriendRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Friend friend) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, friend.getUserId(), friend.getFriendId());
    }

    public void delete(Friend friend) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friend.getUserId(), friend.getFriendId());
    }

    public List<UUID> findById(UUID userId) {
        return jdbcTemplate.query(
                "SELECT f.FRIEND_ID FROM FRIENDSHIP f WHERE f.user_id = ?",
                (rs, rowNum) -> rs.getObject("friend_id", UUID.class),
                userId
        );
    }

}
