package social.network.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import social.network.app.entity.Post;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {

    public PostResponse(Post post) {
        this.id = post.getId();
        this.text = post.getText();
        this.authorUserId = post.getAuthorUserId();
        this.createdAt = post.getCreatedAt();
    }

    @NotNull
    UUID id;
    @NotBlank
    String text;
    @JsonProperty("author_user_id")
    UUID authorUserId;
    @JsonProperty("created_at")
    OffsetDateTime createdAt;
}
