package social.network.app.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import social.network.app.dto.PostDto;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaProducer {

    @Autowired
    private  KafkaTemplate<String, PostDto> kafkaTemplate;

    public CompletableFuture<SendResult<String, PostDto>> sendMessage(String topicName, PostDto message) {
        CompletableFuture<SendResult<String, PostDto>> future =
                kafkaTemplate.send(topicName, message.getPostId(), message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] to topic={} with offset=[{}]",
                        message, topicName, result.getRecordMetadata().offset());
            } else {
                log.warn("Unable to send message=[{}] to topic={} due to {}",
                        message, topicName, ex.getMessage(), ex);
            }
        });

        return future;
    }
}
