package mate.academy.security;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.dto.user.UserLoginResponseDto;
import mate.academy.exception.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto)
            throws AuthenticationException {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDto.email(),
                        userLoginRequestDto.password()));
        return new UserLoginResponseDto(jwtUtil.generateToken(authenticate.getName()));
    }
}
