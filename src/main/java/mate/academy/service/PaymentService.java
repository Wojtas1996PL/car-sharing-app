package mate.academy.service;

import com.stripe.exception.StripeException;
import java.util.List;
import mate.academy.dto.payment.PaymentResponseDto;

public interface PaymentService {
    List<PaymentResponseDto> getPaymentsFromUser(Long userId);

    String handleSuccess(String sessionId) throws StripeException;

    String handleCancel(String sessionId);
}
