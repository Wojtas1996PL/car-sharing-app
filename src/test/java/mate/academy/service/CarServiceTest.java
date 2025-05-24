package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.car.CarDto;
import mate.academy.mapper.CarMapper;
import mate.academy.model.Car;
import mate.academy.model.CarType;
import mate.academy.repository.CarRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    private static Car car;
    private static Car secondCar;
    private static Car updatedCar;
    private static CarDto carDto;
    private static CarDto secondCarDto;
    private static CarDto updatedCarDto;
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarMapper carMapper;
    @Mock
    private CarRepository carRepository;

    @BeforeAll
    public static void createObjects() {
        car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setType(CarType.SUV);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(20.99));

        secondCar = new Car();
        secondCar.setId(2L);
        secondCar.setBrand("Mercedes-Benz");
        secondCar.setModel("CLS");
        secondCar.setType(CarType.UNIVERSAL);
        secondCar.setInventory(100);
        secondCar.setDailyFee(BigDecimal.valueOf(120.99));

        updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setBrand("BMW");
        updatedCar.setModel("X5");
        updatedCar.setType(CarType.SUV);
        updatedCar.setInventory(20);
        updatedCar.setDailyFee(BigDecimal.valueOf(210.99));

        carDto = new CarDto();
        carDto.setId(1L);
        carDto.setBrand("BMW");
        carDto.setModel("X5");
        carDto.setType(CarType.SUV);
        carDto.setInventory(10);
        carDto.setDailyFee(BigDecimal.valueOf(20.99));

        updatedCarDto = new CarDto();
        updatedCarDto.setId(1L);
        updatedCarDto.setBrand("BMW");
        updatedCarDto.setModel("X5");
        updatedCarDto.setType(CarType.SUV);
        updatedCarDto.setInventory(20);
        updatedCarDto.setDailyFee(BigDecimal.valueOf(210.99));

        secondCarDto = new CarDto();
        secondCarDto.setId(2L);
        secondCarDto.setBrand("Mercedes-Benz");
        secondCarDto.setModel("CLS");
        secondCarDto.setType(CarType.UNIVERSAL);
        secondCarDto.setInventory(100);
        secondCarDto.setDailyFee(BigDecimal.valueOf(120.99));
    }

    @Test
    @DisplayName("Verify that method addNewCar works")
    public void addCar_CorrectCar_ReturnsCarDto() {
        when(carMapper.toModel(carDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto carActual = carService.addNewCar(carDto);

        assertThat(carActual).isEqualTo(carDto);

        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toDto(car);
        verify(carMapper, times(1)).toModel(carDto);
    }

    @Test
    @DisplayName("Verify that method getAllCars works")
    public void getAllCars_CorrectList_ReturnsCarDtoList() {
        List<Car> cars = List.of(car, secondCar);
        List<CarDto> expectedCars = List.of(carDto, secondCarDto);

        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.toDto(cars.get(0))).thenReturn(carDto);
        when(carMapper.toDto(cars.get(1))).thenReturn(secondCarDto);

        List<CarDto> actualCars = carService.getAllCars();

        assertThat(actualCars).isEqualTo(expectedCars);

        verify(carRepository, times(1)).findAll();
        verify(carMapper, times(1)).toDto(cars.get(0));
        verify(carMapper, times(1)).toDto(cars.get(1));
    }

    @Test
    @DisplayName("Verify that method getCarInfo works")
    public void getCar_CorrectId_ReturnsCarDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.ofNullable(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto carDtoActual = carService.getCarInfo(car.getId());

        assertThat(carDtoActual).isEqualTo(carDto);

        verify(carRepository, times(1)).findById(car.getId());
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    @DisplayName("Verify that method updateCar works")
    public void updateCar_CorrectCar_ReturnsCarDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.ofNullable(car));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toDto(updatedCar)).thenReturn(updatedCarDto);

        CarDto updatedCarDtoActual = carService.updateCar(car.getId(), updatedCarDto);

        assertThat(updatedCarDtoActual).isEqualTo(updatedCarDto);

        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).save(updatedCar);
        verify(carMapper, times(1)).toDto(updatedCar);
    }
}
