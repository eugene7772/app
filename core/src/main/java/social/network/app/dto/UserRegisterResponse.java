package social.network.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse {
    @JsonProperty("user_id")
    private UUID userId;
}
