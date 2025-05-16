package mate.academy.controller;

import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.rental.RentalDto;
import mate.academy.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Add new rental")
    @PostMapping
    public RentalDto addNewRental(@RequestBody RentalDto rentalDto) {
        return rentalService.addNewRental(rentalDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get rental information from user")
    @GetMapping
    public RentalDto getRentalFromUser(@RequestParam Long userId) {
        return rentalService.getRentalFromUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get specific rental information")
    @GetMapping("/{id}")
    public RentalDto getRentalInfo(@PathVariable Long id) {
        return rentalService.getRentalInfo(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Set actual return date")
    @PostMapping("/return")
    public RentalDto setRentalReturnDate(@RequestParam Long id,
                                         @RequestParam LocalDate returnDate) {
        return rentalService.setRentalReturnDate(id, returnDate);
    }
}
