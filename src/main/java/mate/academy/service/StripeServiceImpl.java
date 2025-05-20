package mate.academy.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentRequestDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Car;
import mate.academy.model.Payment;
import mate.academy.model.PaymentStatus;
import mate.academy.model.PaymentType;
import mate.academy.model.Rental;
import mate.academy.repository.CarRepository;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
class StripeServiceImpl implements StripeService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
    }

    public PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException {
        Rental rental = rentalRepository.findById(paymentRequestDto.getRentalId()).orElseThrow(() ->
                new EntityNotFoundException("Rental not found with id: "
                        + paymentRequestDto.getRentalId()));
        Car car = carRepository.findById(rental.getCarId()).orElseThrow(() ->
                new EntityNotFoundException("Car not found with id: " + rental.getCarId()));

        BigDecimal rentalPrice = car.getDailyFee().multiply(BigDecimal.valueOf(
                rental.getReturnDate().toEpochDay()
                        - rental.getRentalDate().toEpochDay()));

        if (paymentRequestDto.getType() == PaymentType.FINE && !rental.isActive()) {
            int overdueDays = Math.max(0, (int) (rental.getActualReturnDate().toEpochDay()
                    - rental.getReturnDate().toEpochDay()));
            rentalPrice = rentalPrice.add(car.getDailyFee()
                    .multiply(BigDecimal.valueOf(overdueDays)).multiply(FINE_MULTIPLIER));
        }

        String successUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/payments/cancel").toUriString();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(1000L)
                                                .setProductData(
                                                        SessionCreateParams.LineItem
                                                                .PriceData.ProductData.builder()
                                                                .setName("Payment for rental")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Payment payment = new Payment();
        payment.setRentalId(rental.getId());
        payment.setType(paymentRequestDto.getType());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setSessionUrl(new URL(session.getUrl()));
        payment.setSessionId(session.getId());
        paymentRepository.save(payment);

        PaymentResponseDto responseDto = paymentMapper.toResponseDto(payment);
        responseDto.setMoneyToPay(rentalPrice);

        return responseDto;
    }
}
