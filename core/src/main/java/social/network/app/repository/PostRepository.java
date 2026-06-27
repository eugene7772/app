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
import social.network.app.entity.PostStatus;

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
        post.setStatus(PostStatus.valueOf(rs.getString("status")));
        return post;
    };

    public UUID save(Post post) {
        return jdbcTemplate.queryForObject("""
                        INSERT INTO posts (text, author_user_id, created_at, status)
                        VALUES (?, ?, ?, ?)
                        RETURNING id
                        """,
                UUID.class,
                post.getText(),
                post.getAuthorUserId(),
                post.getCreatedAt(),
                post.getStatus().name()
        );
    }

    public void update(Post post) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET text = ?
                        WHERE id = ? AND status = ?;
                        """,
                post.getText(),
                post.getId(),
                PostStatus.ACTIVE.name()
        );
    }

    public void markDeleted(UUID id) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET status = ?
                        WHERE id = ? AND status IN (?, ?)
                        """,
                PostStatus.DELETED.name(),
                id,
                PostStatus.DELETING.name(),
                PostStatus.DELETE_FAILED.name()
        );
    }

    public void markDeleting(UUID id) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET status = ?
                        WHERE id = ? AND status IN (?, ?)
                        """,
                PostStatus.DELETING.name(),
                id,
                PostStatus.ACTIVE.name(),
                PostStatus.DELETE_FAILED.name()
        );
    }

    public void markDeleteFailed(UUID id) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET status = ?
                        WHERE id = ? AND status = ?
                        """,
                PostStatus.DELETE_FAILED.name(),
                id,
                PostStatus.DELETING.name()
        );
    }

    public void markActive(UUID id) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET status = ?
                        WHERE id = ? AND status = ?
                        """,
                PostStatus.ACTIVE.name(),
                id,
                PostStatus.PUBLISHING.name()
        );
    }

    public void markCreationFailed(UUID id) {
        jdbcTemplate.update("""
                        UPDATE posts
                        SET status = ?
                        WHERE id = ? AND status = ?
                        """,
                PostStatus.CREATION_FAILED.name(),
                id,
                PostStatus.PUBLISHING.name()
        );
    }

    public Optional<Post> get(UUID id) {
        String sql = """
                    SELECT id, text, author_user_id, created_at, status
                    FROM posts
                    WHERE id = ? AND status = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, POST_MAPPER, id, PostStatus.ACTIVE.name()));
        } catch (EmptyResultDataAccessException e) {
            log.error(JDBC_ERROR, e);
            return Optional.empty();
        }
    }

    public Optional<Post> getForDelete(UUID id) {
        String sql = """
                    SELECT id, text, author_user_id, created_at, status
                    FROM posts
                    WHERE id = ? AND status IN (?, ?, ?)
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    sql,
                    POST_MAPPER,
                    id,
                    PostStatus.ACTIVE.name(),
                    PostStatus.DELETING.name(),
                    PostStatus.DELETE_FAILED.name()
            ));
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
                    SELECT id, text, author_user_id, created_at, status
                    FROM posts
                    WHERE id IN (:postIds) AND status = :status
                    ORDER BY created_at DESC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("postIds", postIds);
        params.addValue("status", PostStatus.ACTIVE.name());
        return namedParameterJdbcTemplate.query(sql, params, POST_MAPPER);
    }

    public List<Post> getAllLastPostsByUsers(List<UUID> allIds) {
        if (allIds == null || allIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = """
                    SELECT id, text, author_user_id, created_at, status
                    FROM posts
                    WHERE author_user_id IN (:userIds) AND status = :status
                    ORDER BY created_at DESC
                    LIMIT 1000
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userIds", allIds);
        params.addValue("status", PostStatus.ACTIVE.name());
        return namedParameterJdbcTemplate.query(sql, params, POST_MAPPER);
    }

}
