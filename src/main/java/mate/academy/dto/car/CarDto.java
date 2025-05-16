package mate.academy.dto.car;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.model.CarType;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    @Enumerated(EnumType.STRING)
    private CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
