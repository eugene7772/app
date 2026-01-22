package social.network.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String login;
    @JsonProperty("password_hash")
    private String passwordHash;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("second_name")
    private String secondName;
    private LocalDate birthdate;
    private String biography;
    private String city;
}
