package social.network.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import social.network.app.dto.PostDto;
import social.network.app.kafka.producer.KafkaProducer;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducer kafkaProducer;

    @Value("${app.kafka.topic.post-create.name}")
    private String postCreateTopic;

    @Value("${app.kafka.topic.post-delete.name}")
    private String postDeleteTopic;

    public void sendPost(PostDto postDto) {
        kafkaProducer.sendMessage(postCreateTopic, postDto);
    }

    public void sendPostAndWait(PostDto postDto) {
        try {
            kafkaProducer.sendMessage(postCreateTopic, postDto).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Post event sending was interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("Post event was not sent", e);
        }
    }

    public void sendPostDeleted(PostDto postDto) {
        kafkaProducer.sendMessage(postDeleteTopic, postDto);
    }

    public void sendPostDeletedAndWait(PostDto postDto) {
        try {
            kafkaProducer.sendMessage(postDeleteTopic, postDto).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Post delete event sending was interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("Post delete event was not sent", e);
        }
    }
}
