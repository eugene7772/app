package social.network.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import social.network.app.dto.PostDto;
import social.network.app.kafka.producer.KafkaProducer;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducer kafkaProducer;

    @Value("${app.kafka.topic.post-create.name}")
    private String postCreateTopic;

    public void sendPost(PostDto postDto) {
        kafkaProducer.sendMessage(postCreateTopic, postDto);
    }
}
