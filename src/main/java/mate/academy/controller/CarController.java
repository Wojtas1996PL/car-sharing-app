package mate.academy.controller;

import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.car.CarDto;
import mate.academy.service.CarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Add a new car")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    public CarDto addNewCar(@RequestBody CarDto carDto) {
        return carService.addNewCar(carDto);
    }

    @Operation(summary = "Get list of all cars")
    @PermitAll
    @GetMapping
    public List<CarDto> getListOfAllCars() {
        return carService.getListOfAllCars();
    }

    @Operation(summary = "Get information about a car")
    @PermitAll
    @Transactional
    @GetMapping("/{id}")
    public CarDto getCarInfo(@PathVariable Long id) {
        return carService.getCarInfo(id);
    }

    @Operation(summary = "Update car information")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional
    @PutMapping("/{id}")
    public CarDto updateCar(@PathVariable Long id, @RequestBody CarDto carDto) {
        return carService.updateCar(id, carDto);
    }

    @Operation(summary = "Delete a car")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
