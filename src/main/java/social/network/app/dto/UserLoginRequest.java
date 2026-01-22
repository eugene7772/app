package social.network.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    @NotNull
    UUID id;
    @NotBlank
    String login;
    @NotBlank
    String password;
}
