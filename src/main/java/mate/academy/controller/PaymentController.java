package mate.academy.controller;

import com.stripe.exception.StripeException;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentRequestDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.service.PaymentService;
import mate.academy.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final StripeService stripeService;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get list of payments")
    @GetMapping("/myPayments")
    public List<PaymentResponseDto> getPayments() {
        return paymentService.getPayments();
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get list of payments from user")
    @GetMapping
    public List<PaymentResponseDto> getPaymentsFromUser(@RequestParam Long userId) {
        return paymentService.getPaymentsFromUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Create payment session")
    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException {
        return stripeService.createPaymentSession(paymentRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Make successful payment")
    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam String sessionId) {
        return ResponseEntity.ok(paymentService.handleSuccess(sessionId));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Cancel payment")
    @GetMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestParam String sessionId) {
        return ResponseEntity.ok(paymentService.handleCancel(sessionId));
    }
}
