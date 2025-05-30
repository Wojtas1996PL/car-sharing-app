package mate.academy.controller;

import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;
import mate.academy.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Add new rental")
    @PostMapping
    public RentalResponseDto addNewRental(@RequestBody RentalRequestDto rentalRequestDto) {
        return rentalService.addNewRental(rentalRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get list of rentals")
    @GetMapping("/myRentals")
    public List<RentalResponseDto> getRentals(@RequestParam boolean isActive) {
        return rentalService.getRentals(isActive);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get list of rentals from user")
    @GetMapping
    public List<RentalResponseDto> getRentalsFromUser(@RequestParam Long userId,
                                              @RequestParam boolean isActive) {
        return rentalService.getRentalsFromUser(userId, isActive);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get specific rental information")
    @GetMapping("/{id}")
    public RentalResponseDto getRentalInfo(@PathVariable Long id) {
        return rentalService.getRentalInfo(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Set actual return date")
    @PostMapping("/return")
    public RentalResponseDto setRentalReturnDate(@RequestParam Long id,
                                         @RequestParam LocalDate returnDate) {
        return rentalService.setRentalReturnDate(id, returnDate);
    }
}
