package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);
}
