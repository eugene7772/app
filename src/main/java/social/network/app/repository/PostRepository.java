package social.network.app.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import social.network.app.entity.Post;

import java.util.UUID;

@Repository
public class PostRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UUID save(Post post) {
        return jdbcTemplate.queryForObject("""
                        INSERT INTO posts (text, author_user_id)
                        VALUES (?, ?)
                        RETURNING id
                        """,
                UUID.class,
                post.getText(),
                post.getAuthorUserId()
        );
    }

}
