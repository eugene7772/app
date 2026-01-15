package social.network.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @JsonProperty("second_name")
    private String secondName;

    @NotNull
    @Past
    private LocalDate birthdate;

    @Size(max = 500)
    private String biography;

    @NotBlank
    private String city;

    @NotBlank
    @Size(min = 8)
    private String password;
}
