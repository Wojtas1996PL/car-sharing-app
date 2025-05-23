package mate.academy.service;

import java.util.List;
import mate.academy.dto.car.CarDto;

public interface CarService {
    CarDto addNewCar(CarDto carDto);

    List<CarDto> getAllCars();

    CarDto getCarInfo(Long id);

    CarDto updateCar(Long id, CarDto carDto);

    void deleteCar(Long id);
}
