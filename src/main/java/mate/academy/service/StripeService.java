package mate.academy.service;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import mate.academy.dto.payment.PaymentRequestDto;
import mate.academy.dto.payment.PaymentResponseDto;

public interface StripeService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException;
}
