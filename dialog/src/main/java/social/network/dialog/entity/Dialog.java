package social.network.dialog.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Dialog {

    public Dialog(UUID id, UUID user1Id, UUID user12Id) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user12Id;
    }

    private UUID id;
    private UUID user1Id;
    private UUID user2Id;
    private OffsetDateTime createdAt;
}
