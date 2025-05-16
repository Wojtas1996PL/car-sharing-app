package mate.academy.service;

import mate.academy.dto.user.UserDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import mate.academy.model.RoleName;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserDto updateRole(Long userId, RoleName role);

    UserDto getProfileInfo();

    UserDto updateProfileInfo(UserDto userDto);
}
