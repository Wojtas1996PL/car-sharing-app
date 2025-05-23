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
    private final NotificationService notificationService;

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
    public String handleSuccess(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("complete".equals(session.getStatus())) {
                Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                        new EntityNotFoundException("Payment not found"));
                if (payment.getStatus() == PaymentStatus.PAID) {
                    return "Payment has already been processed.";
                }
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                String notificationMessage = "Successful payment!";
                notificationService.sendMessage(notificationMessage);
                return "Payment successful! Your rental is confirmed.";
            }
            return "Payment not completed yet.";
        } catch (StripeException e) {
            return "Payment processing encountered an error.";
        }
    }

    @Override
    public String handleCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Payment not found"));
        if (payment.getStatus() == PaymentStatus.PENDING) {
            String notificationMessage = "Payment was cancelled. You may retry within 24 hours.";
            notificationService.sendMessage(notificationMessage);
            return notificationMessage;
        }
        return "Payment has already been processed and cannot be cancelled.";
    }
}
