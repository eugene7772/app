package social.network.auth.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import social.network.auth.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_BASE_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getObject("id", UUID.class));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password_hash"));
        return user;
    };

    public Optional<User> getBaseById(UUID id) {
        String sql = """
                    SELECT id, login, password_hash
                    FROM users
                    WHERE id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, USER_BASE_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
