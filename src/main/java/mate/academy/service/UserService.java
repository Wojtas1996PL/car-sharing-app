package mate.academy.service;

import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
