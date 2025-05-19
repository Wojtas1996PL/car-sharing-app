package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.rental.RentalDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.InventoryException;
import mate.academy.exception.RentalReturnedException;
import mate.academy.mapper.RentalMapper;
import mate.academy.model.Car;
import mate.academy.model.Rental;
import mate.academy.repository.CarRepository;
import mate.academy.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;

    @Transactional
    @Override
    public RentalDto addNewRental(RentalDto rentalDto) {
        Car car = carRepository.findById(rentalDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + rentalDto.getCarId()));
        if (car.getInventory() == 0) {
            throw new InventoryException("There are no available cars with id: " + car.getId());
        }
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        rentalDto.setActive(true);
        return rentalMapper.toDto(rentalRepository.save(rentalMapper.toModel(rentalDto)));
    }

    @Transactional
    @Override
    public List<RentalDto> getRentalsFromUser(Long userId, boolean isActive) {
        if (rentalRepository.findRentalFromUser(userId, isActive).isEmpty()) {
            throw new EntityNotFoundException("User with id: " + userId + " not found");
        }
        return rentalRepository.findRentalFromUser(userId, isActive).stream()
                .flatMap(Optional::stream)
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RentalDto getRentalInfo(Long id) {
        return rentalMapper.toDto(rentalRepository.getReferenceById(id));
    }

    @Transactional
    @Override
    public RentalDto setRentalReturnDate(Long id, LocalDate returnDate) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: "
                        + id));
        if (!rental.isActive()) {
            throw new RentalReturnedException("Rental with id: " + rental.getId()
                    + " has already been returned");
        }
        rental.setActualReturnDate(returnDate);
        rental.setActive(false);
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + rental.getCarId()));
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
