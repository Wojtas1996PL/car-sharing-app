package mate.academy.service;

import java.time.LocalDate;
import mate.academy.dto.rental.RentalDto;

public interface RentalService {
    RentalDto addNewRental(RentalDto rentalDto);

    RentalDto getRentalFromUser(Long userId);

    RentalDto getRentalInfo(Long id);

    RentalDto setRentalReturnDate(Long id, LocalDate returnDate);
}
