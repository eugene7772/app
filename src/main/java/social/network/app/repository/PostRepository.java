package social.network.app.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import social.network.app.entity.Post;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
@Slf4j
public class PostRepository {
    private static final String JDBC_ERROR = "Post repository error.";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Post> POST_MAPPER = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getObject("id", UUID.class));
        post.setText(rs.getString("text"));
        post.setAuthorUserId(rs.getObject("author_user_id", UUID.class));
        post.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        return post;
    };

    public UUID save(Post post) {
        return jdbcTemplate.queryForObject("""
                        INSERT INTO posts (text, author_user_id, created_at)
                        VALUES (?, ?, ?)
                        RETURNING id
                        """,
                UUID.class,
                post.getText(),
                post.getAuthorUserId(),
                post.getCreatedAt()
        );
    }

    public void update(Post post) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET text = ?
                        WHERE id = ?;
                        """,
                post.getText(),
                post.getId()
        );
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Post> get(UUID id) {
        String sql = "SELECT id, text, author_user_id, created_at FROM posts WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, POST_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            log.error(JDBC_ERROR, e);
            return Optional.empty();
        }
    }

    public List<Post> findAll(List<UUID> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = """
                    SELECT id, text, author_user_id, created_at
                    FROM posts
                    WHERE id IN (:postIds)
                    ORDER BY created_at DESC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("postIds", postIds);
        return namedParameterJdbcTemplate.query(sql, params, POST_MAPPER);
    }

    public List<Post> getAllLastPostsByUsers(List<UUID> allIds) {
        if (allIds == null || allIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = """
                    SELECT id, text, author_user_id, created_at
                    FROM posts
                    WHERE author_user_id IN (:userIds)
                    ORDER BY created_at DESC
                    LIMIT 1000
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userIds", allIds);
        return namedParameterJdbcTemplate.query(sql, params, POST_MAPPER);
    }

}
