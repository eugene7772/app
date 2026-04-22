package social.network.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostDto {
    private String postId;
    private String postText;
    @JsonProperty("author_user_id")
    private String authorUserId;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
