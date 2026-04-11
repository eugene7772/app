package social.network.app.mapper;

import org.springframework.stereotype.Service;
import social.network.app.dto.UserRegisterRequest;
import social.network.app.entity.UserEntity;

@Service
public class UserMapper {
    public UserEntity toEntity(UserRegisterRequest dto, String passwordHash) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(dto.getLogin());
        userEntity.setPasswordHash(passwordHash);
        userEntity.setFirstName(dto.getFirstName());
        userEntity.setSecondName(dto.getSecondName());
        userEntity.setBiography(dto.getBiography());
        userEntity.setCity(dto.getCity());
        userEntity.setBirthdate(dto.getBirthdate());
        return userEntity;
    }
}
