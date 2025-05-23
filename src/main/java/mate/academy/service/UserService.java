package mate.academy.service;

import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import mate.academy.dto.user.UserRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.model.RoleName;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto updateRole(Long userId, RoleName role);

    UserResponseDto getProfileInfo();

    UserResponseDto updateProfileInfo(UserRequestDto userDto);
}
