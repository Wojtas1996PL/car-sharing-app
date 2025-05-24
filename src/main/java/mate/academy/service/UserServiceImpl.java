package mate.academy.service;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import mate.academy.dto.user.UserRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exception.EntityNotFoundException;
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
    public UserResponseDto updateRole(Long userId, RoleName role) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with id: " + userId));
        user.setRole(role);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto getProfileInfo() {
        return userMapper.toUserResponseDto(userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: "
                        + SecurityUtil.getCurrentUserId())));
    }

    @Override
    @Transactional
    public UserResponseDto updateProfileInfo(UserRequestDto userRequestDto) {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: "
                        + SecurityUtil.getCurrentUserId()));
        user.setEmail(userRequestDto.getEmail());
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        return userMapper.toUserResponseDto(userRepository.save(user));
    }
}
