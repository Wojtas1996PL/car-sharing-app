package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import mate.academy.dto.rental.RentalDto;

public interface RentalService {
    RentalDto addNewRental(RentalDto rentalDto);

    List<RentalDto> getRentalsFromUser(Long userId, boolean isActive);

    RentalDto getRentalInfo(Long id);

    RentalDto setRentalReturnDate(Long id, LocalDate returnDate);
}
