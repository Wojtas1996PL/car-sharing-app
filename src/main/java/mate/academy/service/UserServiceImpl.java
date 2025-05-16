package mate.academy.service;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import mate.academy.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            log.warn("User with email {} already exists, registration aborted",
                    requestDto.getEmail());
            throw new RegistrationException("User already exists, cannot register");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(RoleName.ROLE_CUSTOMER);
        return userMapper.toUserRegistrationResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateRole(Long userId, RoleName role) {
        User user = userRepository.getReferenceById(userId);
        user.setRole(role);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto getProfileInfo() {
        return userMapper.toDto(userRepository.getReferenceById(SecurityUtil.getCurrentUserId()));
    }

    @Override
    @Transactional
    public UserDto updateProfileInfo(UserDto userDto) {
        User user = userRepository.getReferenceById(SecurityUtil.getCurrentUserId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPassword(user.getPassword());
        user.setRole(userDto.getRole());
        return userMapper.toDto(userRepository.save(user));
    }
}
