package social.network.app.mapper;

import org.springframework.stereotype.Service;
import social.network.app.dto.UserRegisterRequest;
import social.network.app.entity.UserEntity;
import social.network.app.entity.UserInfo;

import java.util.UUID;

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

    public UserInfo toUserInfo(UUID id, UserRegisterRequest dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setFirstName(dto.getFirstName());
        userInfo.setSecondName(dto.getSecondName());
        userInfo.setBiography(dto.getBiography());
        userInfo.setCity(dto.getCity());
        userInfo.setBirthdate(dto.getBirthdate());
        return userInfo;
    }
}
