package social.network.app.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import social.network.app.dto.PostDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostConsumer {

    @KafkaListener(
            topics = {"app_post_create", "app_post_delete"},
            groupId = "post-service-group"
    )
    public void consume(ConsumerRecord<String, PostDto> record) {
        log.info("Kafka message from topic '{}' offset {} consumed: {}",
                record.topic(), record.offset(), record.value());
    }
}
