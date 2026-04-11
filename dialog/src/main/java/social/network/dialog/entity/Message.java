package social.network.dialog.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private UUID id;
    private UUID dialogId;
    private UUID senderId;
    private UUID recipientId;
    private OffsetDateTime createdAt;
    private String text;
}
