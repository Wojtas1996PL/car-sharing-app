package mate.academy.dto.rental;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDto {
    private Long id;
    private Long carId;
    private Long userId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    @JsonProperty("isActive")
    private boolean isActive;
}
