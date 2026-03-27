package social.network.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @JsonProperty("user_id")
    private UUID userId;
    @JsonProperty("friend_id")
    private UUID friendId;
}
