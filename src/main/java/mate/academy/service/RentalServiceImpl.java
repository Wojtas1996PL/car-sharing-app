package mate.academy.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.rental.RentalDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.RentalMapper;
import mate.academy.model.Rental;
import mate.academy.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    @Override
    public RentalDto addNewRental(RentalDto rentalDto) {
        return rentalMapper.toDto(rentalRepository.save(rentalMapper.toModel(rentalDto)));
    }

    @Transactional
    @Override
    public RentalDto getRentalFromUser(Long userId) {
        return rentalMapper.toDto(
                rentalRepository.findRentalFromUser(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Rental not found for user ID: "
                                        + userId))
        );
    }

    @Transactional
    @Override
    public RentalDto getRentalInfo(Long id) {
        return rentalMapper.toDto(rentalRepository.getReferenceById(id));
    }

    @Transactional
    @Override
    public RentalDto setRentalReturnDate(Long id, LocalDate returnDate) {
        Rental rental = rentalRepository.getReferenceById(id);
        rental.setActualReturnDate(returnDate);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
