package mate.academy.dto.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import mate.academy.model.RoleName;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private RoleName role;
}
