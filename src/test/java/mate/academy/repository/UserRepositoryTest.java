package mate.academy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify that method findUserByEmail works")
    public void findUserByEmail_CorrectEmail_ReturnsTrue() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setEmail("bob@gmail.com");
        user.setPassword("password");
        user.setRole(RoleName.ROLE_CUSTOMER);

        userRepository.save(user);

        Optional<User> actualUser = userRepository.findUserByEmail("bob@gmail.com");

        assertThat(actualUser).isPresent();
    }
}
