package social.network.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private UUID id;
    private String text;
    @JsonProperty("author_user_id")
    private UUID authorUserId;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
