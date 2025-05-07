package mate.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "car_id", nullable = false)
    private Long carId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
}
