package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Payment;
import mate.academy.model.PaymentStatus;
import mate.academy.model.PaymentType;
import mate.academy.model.Rental;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import mate.academy.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    private static User user;
    private static Payment payment1;
    private static Payment payment2;
    private static PaymentResponseDto paymentResponseDto1;
    private static PaymentResponseDto paymentResponseDto2;
    private static Rental rental1;
    private static Rental rental2;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private NotificationService notificationService;

    @BeforeAll
    public static void createObjects() throws MalformedURLException {
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
        rental2.setRentalDate(LocalDate.of(2025,3,7));
        rental2.setReturnDate(LocalDate.of(2025,1,8));
        rental2.setActive(true);

        payment1 = new Payment();
        payment1.setId(1L);
        payment1.setSessionId("sessionId");
        payment1.setSessionUrl(new URL("http://sessionUrl.com"));
        payment1.setRentalId(1L);
        payment1.setType(PaymentType.PAYMENT);
        payment1.setStatus(PaymentStatus.PENDING);

        payment2 = new Payment();
        payment2.setId(2L);
        payment2.setSessionId("sessionId");
        payment2.setSessionUrl(new URL("http://sessionUrl.com"));
        payment2.setRentalId(2L);
        payment2.setType(PaymentType.PAYMENT);
        payment2.setStatus(PaymentStatus.PENDING);

        paymentResponseDto1 = new PaymentResponseDto();
        paymentResponseDto1.setId(1L);
        paymentResponseDto1.setSessionId("sessionId");
        paymentResponseDto1.setSessionUrl("http://sessionUrl.com");
        paymentResponseDto1.setRentalId(1L);
        paymentResponseDto1.setType(PaymentType.PAYMENT);
        paymentResponseDto1.setStatus(PaymentStatus.PENDING);
        paymentResponseDto1.setMoneyToPay(BigDecimal.valueOf(29.99));

        paymentResponseDto2 = new PaymentResponseDto();
        paymentResponseDto2.setId(2L);
        paymentResponseDto2.setSessionId("sessionId");
        paymentResponseDto2.setSessionUrl("http://sessionUrl.com");
        paymentResponseDto2.setRentalId(2L);
        paymentResponseDto2.setType(PaymentType.PAYMENT);
        paymentResponseDto2.setStatus(PaymentStatus.PENDING);
        paymentResponseDto2.setMoneyToPay(BigDecimal.valueOf(19.99));
    }

    @Test
    @DisplayName("Verify that method getPaymentsFromUser works")
    public void getPaymentsFromUser_CorrectPayments_ReturnsPaymentsResponseDtoList() {
        List<Payment> payments = List.of(payment1, payment2);
        List<PaymentResponseDto> expectedPaymentResponseDto =
                List.of(paymentResponseDto1, paymentResponseDto2);

        when(paymentRepository.findPaymentsByUserId(user.getId())).thenReturn(payments);
        when(paymentMapper.toResponseDto(payment1)).thenReturn(paymentResponseDto1);
        when(paymentMapper.toResponseDto(payment2)).thenReturn(paymentResponseDto2);

        List<PaymentResponseDto> actualPaymentsResponseDto = paymentService
                .getPaymentsFromUser(user.getId());

        assertThat(actualPaymentsResponseDto).isEqualTo(expectedPaymentResponseDto);

        verify(paymentRepository, times(1)).findPaymentsByUserId(user.getId());
        verify(paymentMapper, times(1)).toResponseDto(payment1);
        verify(paymentMapper, times(1)).toResponseDto(payment2);
    }

    @Test
    @DisplayName("Verify that method acceptPayment works")
    public void acceptPayment_CorrectsSessionId_ReturnsString() {
        String expectedMessage = "Payment successful! Your rental is confirmed.";

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getStatus()).thenReturn("complete");

            mockedSession.when(() -> Session.retrieve(payment1.getSessionId()))
                    .thenReturn(mockSession);
            when(paymentRepository.findBySessionId(payment1.getSessionId()))
                    .thenReturn(Optional.of(payment1));

            String actualMessage = paymentService.handleSuccess(payment1.getSessionId());

            assertThat(actualMessage).isEqualTo(expectedMessage);
            assertThat(payment1.getStatus()).isEqualTo(PaymentStatus.PAID);

            verify(mockSession, times(1)).getStatus();
            verify(paymentRepository, times(1)).findBySessionId(payment1.getSessionId());
        }
    }

    @Test
    @DisplayName("Verify that method cancelPayment works")
    public void cancelPayment_PendingPayment_ReturnsCancelMessageString() {
        String expectedMessage = "Payment was cancelled. You may retry within 24 hours.";

        when(paymentRepository.findBySessionId(payment2
                .getSessionId())).thenReturn(Optional.of(payment2));
        doNothing().when(notificationService)
                .sendMessage("Payment was cancelled. You may retry within 24 hours.");

        String actualMessage = paymentService.handleCancel(payment1.getSessionId());

        assertThat(actualMessage).isEqualTo(expectedMessage);

        verify(paymentRepository, times(1)).findBySessionId(payment2.getSessionId());
    }
}
