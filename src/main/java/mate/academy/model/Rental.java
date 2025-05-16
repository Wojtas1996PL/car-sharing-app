package mate.academy.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@SQLDelete(sql = "UPDATE rentals SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "rental_cars",
            joinColumns = @JoinColumn(name = "rental_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private List<Car> cars;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    @Column(name = "is_active", nullable = false, columnDefinition = "BIT")
    private boolean isActive;
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT")
    private boolean isDeleted = false;
}
