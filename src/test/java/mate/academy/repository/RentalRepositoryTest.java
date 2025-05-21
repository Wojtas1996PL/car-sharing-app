package mate.academy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Car;
import mate.academy.model.CarType;
import mate.academy.model.Rental;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase
public class RentalRepositoryTest {
    private static User user;
    private static Car car;
    private static Rental rental1;
    private static Rental rental2;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarRepository carRepository;

    @BeforeAll
    public static void createObjects() {
        car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setType(CarType.SUV);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(20.99));

        user = new User();
        user.setId(1L);
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setEmail("bob@gmail.com");
        user.setPassword("password");
        user.setRole(RoleName.ROLE_CUSTOMER);

        rental1 = new Rental();
        rental1.setId(1L);
        rental1.setUserId(1L);
        rental1.setCarId(1L);
        rental1.setRentalDate(LocalDate.of(2025,5,7));
        rental1.setReturnDate(LocalDate.of(2025,5,8));
        rental1.setActive(true);

        rental2 = new Rental();
        rental2.setId(2L);
        rental2.setUserId(1L);
        rental2.setCarId(1L);
        rental2.setRentalDate(LocalDate.of(2025,6,7));
        rental2.setReturnDate(LocalDate.of(2025,4,9));
        rental2.setActive(true);
    }

    @Test
    @DisplayName("Verify that method findRentalsFromUser works")
    public void findRentalsByUserId_CorrectId_ReturnsOptionalList() {
        carRepository.save(car);

        userRepository.save(user);

        rentalRepository.save(rental1);

        rentalRepository.save(rental2);

        List<Rental> expected = List.of(rental1, rental2);

        List<Optional<Rental>> actualOptional = rentalRepository.findRentalsFromUser(1L, true);
        List<Rental> actual = actualOptional.stream().flatMap(Optional::stream).toList();

        assertThat(actual).isEqualTo(expected);
    }
}
