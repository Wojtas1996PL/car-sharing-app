package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;

public interface RentalService {
    RentalResponseDto addNewRental(RentalRequestDto rentalRequestDto);

    List<RentalResponseDto> getRentals(boolean isActive);

    List<RentalResponseDto> getRentalsFromUser(Long userId, boolean isActive);

    RentalResponseDto getRentalInfo(Long id);

    RentalResponseDto setRentalReturnDate(Long id, LocalDate returnDate);
}
