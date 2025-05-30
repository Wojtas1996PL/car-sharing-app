package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;
import mate.academy.exception.InventoryException;
import mate.academy.exception.RentalReturnedException;
import mate.academy.mapper.RentalMapper;
import mate.academy.model.Car;
import mate.academy.model.CarType;
import mate.academy.model.Rental;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import mate.academy.repository.CarRepository;
import mate.academy.repository.RentalRepository;
import mate.academy.security.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static Car car;
    private static Car emptyCar;
    private static Rental rental1;
    private static Rental rental2;
    private static Rental rental3;
    private static Rental rentalNotActive;
    private static User user;
    private static RentalRequestDto rentalRequestDto;
    private static RentalRequestDto rentalRequestDtoEmptyCar;
    private static RentalResponseDto rentalResponseDto1;
    private static RentalResponseDto rentalResponseDto2;
    private static RentalResponseDto rentalResponseDto3;

    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;

    @BeforeAll
    public static void createObjects() {
        car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setType(CarType.SUV);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(20.99));

        emptyCar = new Car();
        emptyCar.setId(2L);
        emptyCar.setBrand("BMW");
        emptyCar.setModel("X3");
        emptyCar.setType(CarType.SUV);
        emptyCar.setInventory(0);
        emptyCar.setDailyFee(BigDecimal.valueOf(20.99));

        user = new User();
        user.setId(1L);
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setEmail("bob@gmail.com");
        user.setPassword("password");
        user.setRole(RoleName.ROLE_CUSTOMER);

        rental1 = new Rental();
        rental1.setId(1L);
        rental1.setUserId(1L);
        rental1.setCarId(1L);
        rental1.setRentalDate(LocalDate.of(2025,5,7));
        rental1.setReturnDate(LocalDate.of(2025,5,8));
        rental1.setActive(true);

        rental2 = new Rental();
        rental2.setId(2L);
        rental2.setUserId(1L);
        rental2.setCarId(1L);
        rental2.setRentalDate(LocalDate.of(2025,4,7));
        rental2.setReturnDate(LocalDate.of(2025,2,8));
        rental2.setActive(true);

        rental3 = new Rental();
        rental3.setId(3L);
        rental3.setUserId(1L);
        rental3.setCarId(1L);
        rental3.setRentalDate(LocalDate.of(2025,4,7));
        rental3.setReturnDate(LocalDate.of(2025,2,8));
        rental3.setActive(true);

        rentalNotActive = new Rental();
        rentalNotActive.setId(4L);
        rentalNotActive.setUserId(1L);
        rentalNotActive.setCarId(1L);
        rentalNotActive.setRentalDate(LocalDate.of(2025,4,7));
        rentalNotActive.setReturnDate(LocalDate.of(2025,2,8));
        rentalNotActive.setActualReturnDate(LocalDate.of(2025, 2, 7));
        rentalNotActive.setActive(false);

        rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.setCarId(1L);
        rentalRequestDto.setRentalDate(LocalDate.of(2025,5,7));
        rentalRequestDto.setReturnDate(LocalDate.of(2025,5,8));

        rentalRequestDtoEmptyCar = new RentalRequestDto();
        rentalRequestDtoEmptyCar.setCarId(2L);
        rentalRequestDtoEmptyCar.setRentalDate(LocalDate.of(2025,5,7));
        rentalRequestDtoEmptyCar.setReturnDate(LocalDate.of(2025,5,8));

        rentalResponseDto1 = new RentalResponseDto();
        rentalResponseDto1.setId(1L);
        rentalResponseDto1.setUserId(1L);
        rentalResponseDto1.setCarId(1L);
        rentalResponseDto1.setRentalDate(LocalDate.of(2025,5,7));
        rentalResponseDto1.setReturnDate(LocalDate.of(2025,5,8));
        rentalResponseDto1.setActive(true);

        rentalResponseDto2 = new RentalResponseDto();
        rentalResponseDto2.setId(2L);
        rentalResponseDto2.setUserId(1L);
        rentalResponseDto2.setCarId(1L);
        rentalResponseDto2.setRentalDate(LocalDate.of(2025,4,7));
        rentalResponseDto2.setReturnDate(LocalDate.of(2025,2,8));
        rentalResponseDto2.setActive(true);

        rentalResponseDto3 = new RentalResponseDto();
        rentalResponseDto3.setId(3L);
        rentalResponseDto3.setUserId(1L);
        rentalResponseDto3.setCarId(1L);
        rentalResponseDto3.setRentalDate(LocalDate.of(2025,4,7));
        rentalResponseDto3.setReturnDate(LocalDate.of(2025,2,8));
        rentalResponseDto3.setActualReturnDate(LocalDate.of(2025,2,8));
        rentalResponseDto3.setActive(false);
    }

    @Test
    @DisplayName("Verify that method add new rental works")
    public void addNewRental_CorrectRentalRequestDto_ReturnsRentalResponseDto() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            when(carRepository.findById(rentalRequestDto.getCarId())).thenReturn(Optional.of(car));
            when(carRepository.save(car)).thenReturn(car);
            mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(user.getId());
            doNothing().when(notificationService)
                    .sendMessage("New rental created! Car id: " + car.getId()
                    + ". User id: " + rental1.getUserId());
            when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
                Rental savedRental = invocation.getArgument(0);
                savedRental.setId(1L);
                return savedRental;
            });
            when(rentalMapper.toResponseDto(rental1)).thenReturn(rentalResponseDto1);

            RentalResponseDto actualRentalResponseDto = rentalService
                    .addNewRental(rentalRequestDto);

            assertThat(actualRentalResponseDto).isEqualTo(rentalResponseDto1);

            verify(carRepository, times(1)).findById(rentalRequestDto.getCarId());
            verify(carRepository, times(1)).save(car);
            verify(rentalRepository, times(1)).save(rental1);
            verify(rentalMapper, times(1)).toResponseDto(rental1);
        }
    }

    @Test
    @DisplayName("Verify that method add new rental throws InventoryException")
    public void addNewRental_RentalRequestDtoWithCarInventoryZero_ThrowsInventoryException() {
        when(carRepository.findById(emptyCar.getId())).thenReturn(Optional.of(emptyCar));

        assertThrows(InventoryException.class, () -> rentalService
                .addNewRental(rentalRequestDtoEmptyCar));
    }

    @Test
    @DisplayName("Verify that method getRentalsFromUser works")
    public void getRentals_ActiveRentals_ReturnsRentalResponseDtoList() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(user.getId());
            List<RentalResponseDto> expectedRentalDtoList = new ArrayList<>();
            expectedRentalDtoList.add(rentalResponseDto1);
            expectedRentalDtoList.add(rentalResponseDto2);
            List<Optional<Rental>> rentals = List.of(Optional.of(rental1), Optional.of(rental2));

            when(rentalRepository.findRentalsFromUser(user.getId(), true)).thenReturn(rentals);
            when(rentalMapper.toResponseDto(rental1)).thenReturn(rentalResponseDto1);
            when(rentalMapper.toResponseDto(rental2)).thenReturn(rentalResponseDto2);

            List<RentalResponseDto> actualRentalsResponseDto = rentalService
                    .getRentals(true);

            assertThat(actualRentalsResponseDto).isEqualTo(expectedRentalDtoList);

            verify(rentalRepository, times(2)).findRentalsFromUser(user.getId(), true);
            verify(rentalMapper, times(1)).toResponseDto(rental1);
            verify(rentalMapper, times(1)).toResponseDto(rental2);
        }
    }

    @Test
    @DisplayName("Verify that method getRentalsFromUser works")
    public void getRentalsFromUser_ActiveRentals_ReturnsRentalResponseDtoList() {
        List<RentalResponseDto> expectedRentalDtoList = new ArrayList<>();
        expectedRentalDtoList.add(rentalResponseDto1);
        expectedRentalDtoList.add(rentalResponseDto2);
        List<Optional<Rental>> rentals = List.of(Optional.of(rental1), Optional.of(rental2));

        when(rentalRepository.findRentalsFromUser(user.getId(), true)).thenReturn(rentals);
        when(rentalMapper.toResponseDto(rental1)).thenReturn(rentalResponseDto1);
        when(rentalMapper.toResponseDto(rental2)).thenReturn(rentalResponseDto2);

        List<RentalResponseDto> actualRentalsResponseDto = rentalService
                .getRentalsFromUser(user.getId(), true);

        assertThat(actualRentalsResponseDto).isEqualTo(expectedRentalDtoList);

        verify(rentalRepository, times(2)).findRentalsFromUser(user.getId(), true);
        verify(rentalMapper, times(1)).toResponseDto(rental1);
        verify(rentalMapper, times(1)).toResponseDto(rental2);
    }

    @Test
    @DisplayName("Verify that method getRentalInfo works")
    public void getRentalInfo_CorrectRental_ReturnsRentalResponseDto() {
        when(rentalRepository.getReferenceById(user.getId())).thenReturn(rental1);
        when(rentalMapper.toResponseDto(rental1)).thenReturn(rentalResponseDto1);

        RentalResponseDto actualRentalResponseDto = rentalService.getRentalInfo(user.getId());

        assertThat(actualRentalResponseDto).isEqualTo(rentalResponseDto1);

        verify(rentalRepository, times(1)).getReferenceById(user.getId());
        verify(rentalMapper, times(1)).toResponseDto(rental1);
    }

    @Test
    @DisplayName("Verify that method setRentalActualReturnDate works")
    public void setRentalActualReturnDate_CorrectDate_ReturnsRentalResponseDto() {
        when(rentalRepository.findById(rental3.getId())).thenReturn(Optional.of(rental3));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental3)).thenReturn(rental3);
        when(rentalMapper.toResponseDto(rental3)).thenReturn(rentalResponseDto3);

        RentalResponseDto actualRentalResponseDto = rentalService
                .setRentalReturnDate(rental3.getId(),LocalDate.of(2025,5,8));

        assertThat(actualRentalResponseDto).isEqualTo(rentalResponseDto3);

        verify(rentalRepository, times(1)).findById(rental3.getId());
        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).save(car);
        verify(rentalRepository, times(1)).save(rental3);
        verify(rentalMapper, times(1)).toResponseDto(rental3);
    }

    @Test
    @DisplayName("Verify that method setActualReturnDate throws RentalReturnedException")
    public void addNewRental_RentalAlreadyReturned_ThrowsRentalReturnedException() {
        when(rentalRepository.findById(4L)).thenReturn(Optional.of(rentalNotActive));

        assertThrows(RentalReturnedException.class, () -> rentalService
                .setRentalReturnDate(4L, LocalDate.of(2025, 2, 8)));
    }
}
