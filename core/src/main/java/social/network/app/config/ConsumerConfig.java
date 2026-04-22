package social.network.app.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Optional;

@Configuration
@Slf4j
public class ConsumerConfig {

    @Bean("defaultKafkaRecordsErrorHandler")
    public DefaultErrorHandler getDefaultKafkaRecordsErrorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }

    private ConsumerRecordRecoverer getRecordErrorProcessor() {
        return new ConsumerRecordRecoverer() {
            @Override
            public void accept(ConsumerRecord<?, ?> rec, Exception ex) {
                Throwable cause = Optional.of(ex).map(Throwable::getCause).orElse(ex);
                log.warn("Exception while kafka topic '{}' record by offset {} processing. Message: {}", rec.topic(), rec.offset(), cause.getMessage());
                ex.printStackTrace();
            }
        };
    }
}

