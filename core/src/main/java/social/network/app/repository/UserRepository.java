package social.network.app.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import social.network.app.entity.User;
import social.network.app.entity.UserEntity;
import social.network.app.entity.UserInfo;

import java.time.LocalDate;
import java.util.List;
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

    private static final RowMapper<UserInfo> USER_INFO_BASE_MAPPER = (rs, rowNum) -> {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(rs.getObject("user_id", UUID.class));
        userInfo.setFirstName(rs.getString("first_name"));
        userInfo.setSecondName(rs.getString("second_name"));
        userInfo.setBirthdate(rs.getObject("birthdate", LocalDate.class));
        userInfo.setBiography(rs.getString("biography"));
        userInfo.setCity(rs.getString("city"));
        return userInfo;
    };

    public UUID save(UserEntity user) {
        return jdbcTemplate.queryForObject("""
                        WITH ins AS (
                          INSERT INTO users (login, password_hash)
                          VALUES (?, ?)
                          RETURNING id
                        )
                        INSERT INTO user_info (user_id, first_name, second_name, birthdate, biography, city)
                        SELECT id, ?, ?, ?, ?, ?
                        FROM ins
                        RETURNING user_id
                        """,
                UUID.class,
                user.getLogin(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getSecondName(),
                user.getBirthdate(),
                user.getBiography(),
                user.getCity()
        );
    }

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

    public Optional<UserInfo> getFullById(UUID userId) {
        String sql = """
                    SELECT user_id, first_name, second_name, birthdate, biography, city
                    FROM user_info
                    WHERE user_id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, USER_INFO_BASE_MAPPER, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<UserInfo>> findByFirstNameAndLastName(String firstName, String lastName) {
        String sql = """
                    SELECT user_id, first_name, second_name, birthdate, biography, city
                    FROM user_info
                    WHERE first_name LIKE ? AND second_name LIKE ?
                """;
        try {
            return Optional.of(jdbcTemplate.query(sql, USER_INFO_BASE_MAPPER, firstName, lastName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
