package mate.academy.dto.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalRequestDto {
    private Long id;
    private Long carId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
}
