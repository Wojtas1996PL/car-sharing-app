package mate.academy.dto.payment;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.model.PaymentStatus;
import mate.academy.model.PaymentType;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal moneyToPay;
}
