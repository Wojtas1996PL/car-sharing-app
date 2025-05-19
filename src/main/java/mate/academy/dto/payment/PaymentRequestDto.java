package mate.academy.dto.payment;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import mate.academy.model.PaymentType;

@Data
public class PaymentRequestDto {
    private Long rentalId;
    @Enumerated(EnumType.STRING)
    private PaymentType type;
}
