package mate.academy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.car.CarDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CarMapper;
import mate.academy.model.Car;
import mate.academy.repository.CarRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto addNewCar(CarDto carDto) {
        return carMapper.toDto(carRepository.save(carMapper.toModel(carDto)));
    }

    @Override
    public List<CarDto> getAllCars() {
        return carRepository
                .findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto getCarInfo(Long id) {
        return carMapper.toDto(carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id)));
    }

    @Override
    public CarDto updateCar(Long id, CarDto carDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setBrand(carDto.getBrand());
        car.setType(carDto.getType());
        car.setModel(carDto.getModel());
        car.setInventory(carDto.getInventory());
        car.setDailyFee(carDto.getDailyFee());
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}
