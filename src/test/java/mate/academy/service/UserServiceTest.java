package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import mate.academy.dto.user.UserRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.mapper.UserMapper;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import mate.academy.security.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static User user1;
    private static User user2;
    private static User user3;
    private static User manager;
    private static UserResponseDto user1ResponseDto;
    private static UserResponseDto user3ResponseDto;
    private static UserRequestDto user3RequestDto;
    private static UserResponseDto managerResponseDto;
    private static UserRegistrationRequestDto userRegistrationRequestDto;
    private static UserRegistrationResponseDto userRegistrationResponseDto;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public static void createObjects() {
        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Bob");
        user1.setLastName("Marley");
        user1.setEmail("bob@gmail.com");
        user1.setPassword("password");
        user1.setRole(RoleName.ROLE_CUSTOMER);

        user1ResponseDto = new UserResponseDto();
        user1ResponseDto.setId(1L);
        user1ResponseDto.setFirstName("Bob");
        user1ResponseDto.setLastName("Marley");
        user1ResponseDto.setEmail("bob@gmail.com");
        user1ResponseDto.setRole(RoleName.ROLE_CUSTOMER);

        user2 = new User();
        user2.setId(1L);
        user2.setFirstName("Bob");
        user2.setLastName("Marley");
        user2.setEmail("bob@gmail.com");
        user2.setPassword("password");
        user2.setRole(RoleName.ROLE_CUSTOMER);

        user3 = new User();
        user3.setId(1L);
        user3.setFirstName("Bob");
        user3.setLastName("Marley");
        user3.setEmail("bob@gmail.com");
        user3.setPassword("password");
        user3.setRole(RoleName.ROLE_CUSTOMER);

        user3ResponseDto = new UserResponseDto();
        user3ResponseDto.setId(1L);
        user3ResponseDto.setFirstName("Bob");
        user3ResponseDto.setLastName("Marley");
        user3ResponseDto.setEmail("bob@gmail.com");
        user3ResponseDto.setRole(RoleName.ROLE_CUSTOMER);

        user3RequestDto = new UserRequestDto();
        user3RequestDto.setFirstName("Bob");
        user3RequestDto.setLastName("Marley");
        user3RequestDto.setEmail("bob@gmail.com");

        manager = new User();
        manager.setId(1L);
        manager.setFirstName("Bob");
        manager.setLastName("Marley");
        manager.setEmail("bob@gmail.com");
        manager.setPassword("password");
        manager.setRole(RoleName.ROLE_MANAGER);

        managerResponseDto = new UserResponseDto();
        managerResponseDto.setId(1L);
        managerResponseDto.setFirstName("Bob");
        managerResponseDto.setLastName("Marley");
        managerResponseDto.setEmail("bob@gmail.com");
        managerResponseDto.setRole(RoleName.ROLE_MANAGER);

        userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setFirstName("Bob");
        userRegistrationRequestDto.setLastName("Marley");
        userRegistrationRequestDto.setEmail("bob@gmail.com");
        userRegistrationRequestDto.setPassword("password");
        userRegistrationRequestDto.setRepeatPassword("password");

        userRegistrationResponseDto = new UserRegistrationResponseDto();
        userRegistrationResponseDto.setId(1L);
        userRegistrationResponseDto.setEmail("bob@gmail.com");
    }

    @Test
    @DisplayName("Verify that method register works")
    public void register_CorrectUserRegistrationRequestDto_ReturnsUserRegistrationResponseDto() {
        when(userRepository.findUserByEmail(userRegistrationRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRegistrationRequestDto
                .getPassword())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(userMapper.toUserRegistrationResponseDto(user1))
                .thenReturn(userRegistrationResponseDto);

        UserRegistrationResponseDto actualRegistrationResponseDto = userService
                .register(userRegistrationRequestDto);

        assertThat(actualRegistrationResponseDto).isEqualTo(userRegistrationResponseDto);

        verify(userRepository, times(1))
                .findUserByEmail(userRegistrationRequestDto.getEmail());
        verify(userRepository, times(1)).save(user1);
        verify(userMapper, times(1)).toUserRegistrationResponseDto(user1);
    }

    @Test
    @DisplayName("Verify that method updateRole works")
    public void updateRole_CorrectRole_ReturnsUserDto() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(userRepository.save(user2)).thenReturn(manager);
        when(userMapper.toUserResponseDto(manager)).thenReturn(managerResponseDto);

        UserResponseDto actualUserDto = userService
                .updateRole(user2.getId(), RoleName.ROLE_MANAGER);

        assertThat(actualUserDto).isEqualTo(managerResponseDto);

        verify(userRepository, times(1)).findById(user2.getId());
        verify(userRepository, times(1)).save(user2);
        verify(userMapper, times(1)).toUserResponseDto(manager);
    }

    @Test
    @DisplayName("Verify that method getProfileInfo works")
    public void getProfileInfo_CorrectUser_ReturnsUserDto() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(user1.getId());
            when(userRepository.findById(SecurityUtil.getCurrentUserId()))
                    .thenReturn(Optional.of(user1));
            when(userMapper.toUserResponseDto(user1)).thenReturn(user1ResponseDto);

            UserResponseDto actualUserResponseDto = userService.getProfileInfo();

            assertThat(actualUserResponseDto).isEqualTo(user1ResponseDto);

            verify(userRepository, times(1)).findById(SecurityUtil.getCurrentUserId());
            verify(userMapper, times(1)).toUserResponseDto(user1);
        }
    }

    @Test
    @DisplayName("Verify that method updateProfileInfo works")
    public void updateProfileInfo_CorrectUser_ReturnsUserDto() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(user1.getId());
            when(userRepository.findById(SecurityUtil.getCurrentUserId()))
                    .thenReturn(Optional.of(user3));
            when(userRepository.save(user3)).thenReturn(user3);
            when(userMapper.toUserResponseDto(user3)).thenReturn(user3ResponseDto);

            UserResponseDto actualUserResponseDto = userService.updateProfileInfo(user3RequestDto);

            assertThat(actualUserResponseDto).isEqualTo(user3ResponseDto);

            verify(userRepository, times(1)).findById(SecurityUtil.getCurrentUserId());
            verify(userRepository, times(1)).save(user3);
            verify(userMapper, times(1)).toUserResponseDto(user3);
        }
    }
}
