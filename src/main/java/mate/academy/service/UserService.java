package mate.academy.service;

import mate.academy.dto.UserDto;
import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserRegistrationResponseDto;
import mate.academy.model.RoleName;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserDto updateRole(Long userId, RoleName role);

    UserDto getProfileInfo();

    UserDto updateProfileInfo(UserDto userDto);
}
