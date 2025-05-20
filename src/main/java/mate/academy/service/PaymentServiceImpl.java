package mate.academy.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Payment;
import mate.academy.model.PaymentStatus;
import mate.academy.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponseDto> getPaymentsFromUser(Long userId) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(userId);
        if (payments.isEmpty()) {
            throw new EntityNotFoundException("Payments with user id: " + userId + " not found");
        }
        return payments.stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public String handleSuccess(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        if ("complete".equals(session.getStatus())) {
            Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                    new EntityNotFoundException("Payment not found"));
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            return "Payment successful! Your rental is confirmed.";
        }
        return "Payment not completed yet.";
    }

    @Override
    public String handleCancel(String sessionId) {
        return "Payment was cancelled, you can retry within 24 hours";
    }
}
