package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.InventoryException;
import mate.academy.exception.RentalReturnedException;
import mate.academy.mapper.RentalMapper;
import mate.academy.model.Car;
import mate.academy.model.Rental;
import mate.academy.repository.CarRepository;
import mate.academy.repository.RentalRepository;
import mate.academy.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public RentalResponseDto addNewRental(RentalRequestDto rentalRequestDto) {
        Car car = carRepository.findById(rentalRequestDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + rentalRequestDto.getCarId()));
        if (car.getInventory() == 0) {
            throw new InventoryException("There are no available cars with id: " + car.getId());
        }
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        RentalResponseDto rentalResponseDto = rentalMapper.toResponseDto(rentalRequestDto);
        rentalResponseDto.setUserId(SecurityUtil.getCurrentUserId());
        rentalResponseDto.setActive(true);
        String notificationMessage = "New rental created! Car id: " + car.getId()
                + ". User id: " + rentalResponseDto.getUserId();
        notificationService.sendMessage(notificationMessage);
        return rentalMapper.toResponseDto(rentalRepository
                .save(rentalMapper.toModel(rentalResponseDto)));
    }

    @Transactional
    @Override
    public List<RentalResponseDto> getRentalsFromUser(Long userId, boolean isActive) {
        if (rentalRepository.findRentalsFromUser(userId, isActive).isEmpty()) {
            throw new EntityNotFoundException("User with id: " + userId + " not found");
        }
        return rentalRepository.findRentalsFromUser(userId, isActive).stream()
                .flatMap(Optional::stream)
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RentalResponseDto getRentalInfo(Long id) {
        return rentalMapper.toResponseDto(rentalRepository.getReferenceById(id));
    }

    @Transactional
    @Override
    public RentalResponseDto setRentalReturnDate(Long id, LocalDate returnDate) {
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
        return rentalMapper.toResponseDto(rentalRepository.save(rental));
    }
}
