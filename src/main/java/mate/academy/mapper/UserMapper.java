package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.UserDto;
import mate.academy.dto.UserRegistrationResponseDto;
import mate.academy.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toUserRegistrationResponseDto(User user);

    UserDto toDto(User user);
}
