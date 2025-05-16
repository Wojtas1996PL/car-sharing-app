package mate.academy.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Length(min = 8, max = 20)
        String password) {
}
