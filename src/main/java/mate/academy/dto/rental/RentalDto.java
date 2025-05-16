package mate.academy.dto.rental;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import mate.academy.dto.car.CarDto;
import mate.academy.dto.user.UserDto;

@Data
public class RentalDto {
    private Long id;
    private List<CarDto> cars;
    private UserDto user;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private boolean isActive;
}
