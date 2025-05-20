package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Rental;
import mate.academy.repository.RentalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class RentalOverdueCheckServiceImpl implements RentalOverdueCheckService {
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    @Override
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository.findAll().stream()
                .filter(rental -> rental.isActive() && rental.getReturnDate().isBefore(today))
                .toList();

        if (overdueRentals.isEmpty()) {
            notificationService.sendMessage("No rentals overdue today!");
        } else {
            overdueRentals.forEach(rental -> {
                String message = "**Overdue Rental Alert!**\n"
                        + "Car id: " + rental.getCarId() + "\n"
                        + "User id: " + rental.getUserId() + "\n"
                        + "Return date: " + rental.getReturnDate();
                notificationService.sendMessage(message);
            });
        }
    }
}
