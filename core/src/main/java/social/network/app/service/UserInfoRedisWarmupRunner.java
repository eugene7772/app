package social.network.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import social.network.app.entity.UserInfo;
import social.network.app.repository.UserInfoRedisRepository;
import social.network.app.repository.UserRepository;

import java.util.List;

@Component
@Slf4j
public class UserInfoRedisWarmupRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRedisRepository userInfoRedisRepository;

    @Value("${app.redis.user-info-cache.warmup-enabled:false}")
    private boolean warmupEnabled;

    @Value("${app.redis.user-info-cache.warmup-batch-size:1000}")
    private int batchSize;

    @Override
    public void run(ApplicationArguments args) {
        if (!warmupEnabled) {
            log.info("User info Redis warmup is disabled.");
            return;
        }

        int offset = 0;
        int total = 0;
        while (true) {
            List<UserInfo> batch = userRepository.findUserInfoBatch(batchSize, offset);
            if (batch.isEmpty()) {
                break;
            }
            batch.forEach(userInfoRedisRepository::save);
            total += batch.size();
            offset += batchSize;
            log.info("User info Redis warmup progress: {}", total);
        }
        log.info("User info Redis warmup finished. Loaded {} users.", total);
    }
}
