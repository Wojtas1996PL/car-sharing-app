package mate.academy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import mate.academy.model.Car;
import mate.academy.model.CarType;
import mate.academy.model.Payment;
import mate.academy.model.PaymentStatus;
import mate.academy.model.PaymentType;
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
public class PaymentRepositoryTest {
    private static User user;
    private static Payment payment1;
    private static Payment payment2;
    private static Rental rental1;
    private static Rental rental2;
    private static Car car;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void createObjects() throws MalformedURLException {
        user = new User();
        user.setId(1L);
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setEmail("bob@gmail.com");
        user.setPassword("password");
        user.setRole(RoleName.ROLE_CUSTOMER);

        payment1 = new Payment();
        payment1.setId(1L);
        payment1.setStatus(PaymentStatus.PAID);
        payment1.setSessionId("session1");
        payment1.setType(PaymentType.PAYMENT);
        payment1.setSessionUrl(new URL("http://session.com"));
        payment1.setRentalId(1L);

        payment2 = new Payment();
        payment2.setId(2L);
        payment2.setStatus(PaymentStatus.PENDING);
        payment2.setSessionId("session2");
        payment2.setType(PaymentType.PAYMENT);
        payment2.setSessionUrl(new URL("http://session.com"));
        payment2.setRentalId(2L);

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

        car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setType(CarType.SUV);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(20.99));
    }

    @Test
    @DisplayName("Verify that method findPaymentsByUserId works")
    public void findPaymentsByUserId_CorrectId_ReturnsPaymentsList() {
        carRepository.save(car);
        userRepository.save(user);
        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        List<Payment> expectedPayments = List.of(payment1, payment2);

        List<Payment> actualPayments = paymentRepository.findPaymentsByUserId(1L);

        assertThat(actualPayments).isEqualTo(expectedPayments);
    }

    @Test
    @DisplayName("Verify that method findBySessionId works")
    public void findPaymentBySessionId_CorrectId_ReturnsPaymentOptional() {
        carRepository.save(car);
        userRepository.save(user);
        rentalRepository.save(rental1);
        paymentRepository.save(payment1);

        Payment expectedPayment = payment1;

        Payment actualPayment = paymentRepository.findBySessionId("session1")
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        assertThat(actualPayment).isEqualTo(expectedPayment);
    }
}
