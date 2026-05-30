package social.network.app.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import social.network.app.entity.UserInfo;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class UserInfoRedisRepository {

    private static final String USER_INFO_KEY_PREFIX = "user:info:";
    private static final String USER_SEARCH_KEY_PREFIX = "user:search:";
    private static final String USER_SEARCH_JSON_KEY_PREFIX = "user:search-json:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final DefaultRedisScript<Long> saveUserInfoScript;

    public UserInfoRedisRepository() {
        saveUserInfoScript = new DefaultRedisScript<>();
        saveUserInfoScript.setLocation(new ClassPathResource("redis/save-user-info.lua"));
        saveUserInfoScript.setResultType(Long.class);
    }

    public void save(UserInfo userInfo) {
        String userJson = toJson(userInfo);
        redisTemplate.execute(
                saveUserInfoScript,
                List.of(
                        buildUserInfoKey(userInfo.getId()),
                        buildSearchKey(userInfo.getFirstName(), userInfo.getSecondName()),
                        buildSearchJsonKey(userInfo.getFirstName(), userInfo.getSecondName())
                ),
                userJson
        );
    }

    public List<UserInfo> findByFirstNameAndSecondName(String firstName, String secondName) {
        Set<String> users = redisTemplate.opsForZSet().range(buildSearchKey(firstName, secondName), 0, -1);
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(this::fromJson)
                .toList();
    }

    public String findJsonByFirstNameAndSecondName(String firstName, String secondName) {
        String searchJsonKey = buildSearchJsonKey(firstName, secondName);
        String cachedResponse = redisTemplate.opsForValue().get(searchJsonKey);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        Set<String> users = redisTemplate.opsForZSet().range(buildSearchKey(firstName, secondName), 0, -1);
        String response = users == null || users.isEmpty() ? "[]" : "[" + String.join(",", users) + "]";
        redisTemplate.opsForValue().set(searchJsonKey, response);
        return response;
    }

    public Optional<UserInfo> findById(UUID id) {
        String userJson = redisTemplate.opsForValue().get(buildUserInfoKey(id));
        return userJson == null ? Optional.empty() : Optional.of(fromJson(userJson));
    }

    private String buildUserInfoKey(UUID id) {
        return USER_INFO_KEY_PREFIX + id;
    }

    private String buildSearchKey(String firstName, String secondName) {
        return USER_SEARCH_KEY_PREFIX + normalize(firstName) + ":" + normalize(secondName);
    }

    private String buildSearchJsonKey(String firstName, String secondName) {
        return USER_SEARCH_JSON_KEY_PREFIX + normalize(firstName) + ":" + normalize(secondName);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String toJson(UserInfo userInfo) {
        try {
            return objectMapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize user info", e);
        }
    }

    private UserInfo fromJson(String userJson) {
        try {
            return objectMapper.readValue(userJson, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to deserialize user info", e);
        }
    }
}
