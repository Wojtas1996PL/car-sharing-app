package mate.academy.service;

import java.util.List;
import mate.academy.dto.payment.PaymentResponseDto;

public interface PaymentService {
    List<PaymentResponseDto> getPayments();

    List<PaymentResponseDto> getPaymentsFromUser(Long userId);

    String handleSuccess(String sessionId);

    String handleCancel(String sessionId);
}
